package com.example.shop.controller;

import cn.hutool.core.lang.Snowflake;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shop.base.BaseOrder;
import com.example.shop.contants.QueueConstants;
import com.example.shop.domain.*;
import com.example.shop.entity.R;
import com.example.shop.entity.WxMsg;
import com.example.shop.feign.CartServiceFeign;
import com.example.shop.feign.MemberServiceFeign;
import com.example.shop.feign.ProductServiceFeign;
import com.example.shop.properties.WxProperties;
import com.example.shop.service.OrderItemService;
import com.example.shop.service.OrderService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: William
 * @date: 2023-06-15 18:22
 **/
@RestController
@RequestMapping("/p/myOrder")
@RequiredArgsConstructor
public class OrderController extends BaseOrder {

    private final OrderService orderService;

    private final ProductServiceFeign productServiceFeign;

    private final CartServiceFeign cartServiceFeign;

    private final MemberServiceFeign memberServiceFeign;

    private final Snowflake snowflake;

    private final RabbitTemplate rabbitTemplate;

    private final OrderItemService orderItemService;

    private final WxProperties wxProperties;

    private final WxMsg wxMsg;


    @GetMapping("/query")
    public R<Boolean> queryOrderType(@Param("orderSn") String orderNumber) {
        //根据订单号查询订单信息
        Order order = orderService.getOne(
                new LambdaQueryWrapper<Order>()
                        .eq(StringUtils.isNotBlank(orderNumber), Order::getOrderNumber, orderNumber)
        );

        return ok(
                order.getIsPayed() ==  1 ? true : false
        );
    }


    /**
     * 展示订单确认页面的数据
     * 将OrderConfirm转换为OrderVo对象
     * <p>
     * OrderConfirm
     * basketIds：购物车页面进入，传递的参数
     * orderItem：商品详情页面进入
     */
    @PostMapping("/confirm")
    public R<OrderVo> confirm(@RequestBody OrderConfirm orderConfirm) {
        //获取购物车的ids集合
        List<Long> basketIds = orderConfirm.getBasketIds();

        //创建封装的vo对象
        OrderVo orderVo = new OrderVo();

        if (CollectionUtils.isEmpty(basketIds))
            //从商品详情页面进入
            translateDataFromProductDetails(orderConfirm.getOrderItem(), orderVo);
        else
            //从购物车页面进入
            translateDataFromCart(basketIds, orderVo);

        return ok(orderVo);

    }

    private void translateDataFromCart(List<Long> basketIds, OrderVo orderVo) {
        //参数1，代表购物车ids集合
        //参数2，订单详情对象
        //参数3，封装的vo对象
        translate(basketIds, null, orderVo);
    }

    private void translateDataFromProductDetails(OrderItem orderItem, OrderVo orderVo) {
        String userId = getWxUserId();

        //创建商品列表集合数据，封装具体的订单详情数据
        List<OrderItem> orderItemList = new ArrayList<>();

        //远程调用
        List<Sku> skuList = productServiceFeign.skuList(Arrays.asList(orderItem.getSkuId()));

        Sku sku = skuList.get(0);

        //计算商品总金额
        BigDecimal totalAmount = sku.getPrice().multiply(new BigDecimal(orderItem.getProdCount()));

        orderItemList.add(
                orderItem.setShopId(orderItem.getShopId())
                        //订单号，不用设置，创建订单时设置的
                        //.orderNumber
                        .setProdId(sku.getProdId())
                        .setSkuId(sku.getSkuId())
                        .setProdCount(orderItem.getProdCount())
                        .setProdName(sku.getProdName())
                        .setSkuName(sku.getSkuName())
                        .setPic(sku.getPic())
                        .setPrice(sku.getPrice())
                        .setUserId(userId)
                        .setRecTime(new Date())
                        .setCommSts(0)
                        .setBasketDate(new Date())
                        .setProductTotalAmount(totalAmount)
        );
        translate(null,orderItemList,orderVo);
    }

    private void translate(List<Long> basketIds, List<OrderItem> orderItemList, OrderVo orderVo) {

        //获取用户Id
        String userId = getWxUserId();

        //根据用户id查询用户默认的收货地址
        UserAddr userAddr = memberServiceFeign.defaultUserAddr(userId);

        //封装用户默认收货地址
        orderVo.setUserAddr(userAddr);

        //创建vo对象所需依赖的子对象
        List<ShopOrder> shopOrders = new ArrayList<>();

        //封装到vo对象中
        orderVo.setShopCartOrders(shopOrders);

        //封装页面来源的数据(商品详情页面)到集合中
        if (CollectionUtils.isEmpty(basketIds)) {
            ShopOrder shopOrder = new ShopOrder();

            shopOrders.add(shopOrder);

            shopOrder.setShopCartItemDiscounts(orderItemList);

            //计算运费、折扣
            BigDecimal amount = orderItemList.stream().map(OrderItem::getProductTotalAmount).collect(Collectors.toList())
                    .stream().reduce(BigDecimal::add).get();

            if (amount.compareTo(new BigDecimal(99)) < 0)
                shopOrder.setTransfee(new BigDecimal(6));

            //封装vo对象其他属性信息
            orderVo.setTotalCount(orderItemList.get(0).getProdCount())
                    .setTotal(amount)
                    .setActualTotal(
                            amount.add(shopOrder.getShopReduce()).add(shopOrder.getTransfee())
                    )
                    .setShopReduce(shopOrder.getShopReduce())
                    .setTransfee(shopOrder.getTransfee());
        } else {
            //封装页面来源的数据(购物车列表页面)到集合中
            //根据购物车ids数据，查询集合数据
            List<Basket> basketList = cartServiceFeign.list(basketIds);

            //获取skuIds
            List<Long> skuIds = basketList.stream().map(Basket::getSkuId).collect(Collectors.toList());

            //根据skuIds，获取sku集合数据
            List<Sku> skuList = productServiceFeign.skuList(skuIds);

            //将vo对象的数据进行封装
            //将basketList转换为以店铺id和对应数据的列表数据
            Map<Long, List<Basket>> shopsMap = basketList.stream().collect(Collectors.groupingBy(Basket::getShopId));

            List<Integer> totalCountList = new ArrayList<>();
            List<BigDecimal> totalPriceList = new ArrayList<>();
            List<BigDecimal> finalPriceList = new ArrayList<>();
            List<BigDecimal> shopReduceList = new ArrayList<>();
            List<BigDecimal> transfeeList = new ArrayList<>();

            shopsMap.forEach(
                    (shopId, baskets) -> {

                        //创建店铺对象
                        ShopOrder shopOrder = new ShopOrder();

                        //将店铺对象添加到新创建的vo子对象中
                        shopOrders.add(shopOrder);

                        List<OrderItem> orderItems = new ArrayList<>();
                        shopOrder.setShopCartItemDiscounts(orderItems);

                        //创建封装店铺总金额的集合
                        List<BigDecimal> shopAmount = new ArrayList<>();

                        //遍历购物车列表数据，封装订单详情列表数据
                        baskets.forEach(
                                basket -> {
                                    //将当前的sku过滤出来
                                    List<Sku> skus = skuList.stream().filter(sku -> sku.getSkuId().equals(basket.getSkuId())).collect(Collectors.toList());

                                    if (!CollectionUtils.isEmpty(skus)) {
                                        //获取sku对象
                                        Sku sku = skus.get(0);

                                        //计算商品总金额
                                        BigDecimal totalAmount = sku.getPrice().multiply(new BigDecimal(basket.getBasketCount()));

                                        orderItems.add(
                                                OrderItem.builder()
                                                        .shopId(shopId)
                                                        //订单号，不用设置，创建订单时设置的
                                                        //.orderNumber()
                                                        .prodId(basket.getProdId())
                                                        .skuId(basket.getSkuId())
                                                        .prodCount(basket.getBasketCount())
                                                        .prodName(sku.getProdName())
                                                        .skuName(sku.getSkuName())
                                                        .pic(sku.getPic())
                                                        .price(sku.getPrice())
                                                        .userId(userId)
                                                        .recTime(new Date())
                                                        .commSts(0)
                                                        .basketDate(new Date())
                                                        .productTotalAmount(totalAmount)
                                                        .build()
                                        );

                                        shopAmount.add(totalAmount);

                                        //封装商品数量
                                        totalCountList.add(basket.getBasketCount());
                                    }
                                }
                        );

                        //计算运费、折扣
                        BigDecimal amount = shopAmount.stream().reduce(BigDecimal::add).get();

                        if (amount.compareTo(new BigDecimal(99)) < 0)
                            shopOrder.setTransfee(new BigDecimal(6));

                        finalPriceList.add(amount);
                        totalPriceList.add(amount);
                        shopReduceList.add(shopOrder.getShopReduce());
                        transfeeList.add(shopOrder.getTransfee());

                    }
            );

            //封装vo对象其他属性信息
            Integer totalCount = totalCountList.stream().reduce(Integer::sum).get();
            BigDecimal total = totalPriceList.stream().reduce(BigDecimal::add).get();
            BigDecimal actualTotal = finalPriceList.stream().reduce(BigDecimal::add).get();
            BigDecimal shopReduce = shopReduceList.stream().reduce(BigDecimal::add).get();
            BigDecimal transfee = transfeeList.stream().reduce(BigDecimal::add).get();

            orderVo.setTotalCount(totalCount)
                    .setTotal(total)
                    .setActualTotal(
                            actualTotal.add(shopReduce).add(transfee)
                    )
                    .setShopReduce(shopReduce)
                    .setTransfee(transfee);

        }
    }

    /**
     * 提交订单
     *  1. 生成订单号，分布式ID生成，可以排序的，不会重复的，而且生成的速率比较快
     *      指定工作号、数据中心，就可以不同的集群生成重复订单号
     *      也可以通过时间戳(年月日时分秒毫秒+随机数或Redis自增)
     *  2. 清空购物车(忽略)
     *  3.扣减Mysql的库存
     *  prod、sku
     *  4. 扣减ES的库存
     *  prodEs
     *  5. 生成订单信息
     *  创建order及orderItem
     *  6. 发送微信公众号消息，通知用户下单
     *  7. 消息的补回
     *  如果用户下单，长时间(2分钟、5分钟、30分钟、2小时、24小时)，取消订单，库存信息补回Mysql及Es
     * @param orderVo
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody OrderVo orderVo) {
    //1.生成订单号，分布式ID生成
    String orderNum = createOrderNum();

    //2.清空购物车(忽略)
    List<ProdEsCount> countList = clearCart(orderVo);

    //3.扣减Mysql的库存
    deductMySqlStock(countList);

    //4. 扣减ES的库存
    deductEsStock(countList);

    //5. 生成订单信息
    String orderName = createOrderAndItems(orderNum, orderVo);

    //6. 发送微信公众号消息
    sendWxMsg(orderVo,orderName);

    //7. 消息的补回
        /**
         * 延迟队列：发送消息到延迟队列中，当消息超时未处理，交给延迟队列处理
         * 死信队列：消息补回操作
         */
        sendCountList2Queue(orderNum, countList);

        return ok("orderNum:" + orderNum);
    }

    private void sendCountList2Queue(String orderNum, List<ProdEsCount> countList) {
        //封装Map集合传递
        Map<String, List<ProdEsCount>> data = new HashMap<>();
        data.put(orderNum, countList);

        //发送消息到延迟队列，消息超时交给死信队列进行处理
        rabbitTemplate.convertAndSend(
                QueueConstants.ORDER_MS_QUEUE,
                JSON.toJSONString(
                        data
                )
        );
    }

    private void sendWxMsg(OrderVo orderVo, String orderName) {
        //根据userId查询微信模板公众号的openid
        User user = memberServiceFeign.getUser(getWxUserId());

        //封装WxMsg
        WxMsg.builder()
                .touser(user.getWxTemplateOpenId())
                .template_id(wxProperties.getTemplateId())
                .topcolor("#FF0000")
                .url("http://www.baidu.com")
                .build();

        //封装data，也就是微信公众号的具体显示的内容
        wxMsg.appenData("time", "#173177", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        wxMsg.appenData("goods","#173177",orderName);
        wxMsg.appenData("price","#173177",orderVo.getActualTotal().toPlainString());
        wxMsg.appenData("money", "#173177", orderVo.getTotal().toPlainString());

        //发送到消息队列中
        rabbitTemplate.convertAndSend(
                QueueConstants.WX_MSG_QUEUE,
                JSON.toJSONString(wxMsg)
        );
    }

    private String createOrderAndItems(String orderNum, OrderVo orderVo) {

        StringBuilder builder = new StringBuilder();

        List<OrderItem> orderItemList = orderVo.getShopCartOrders()
                .stream()
                //将所有的店铺的集合收集到一起
                .map(ShopOrder::getShopCartItemDiscounts)
                //收集后的数据List<List<OrderItem>>
                .collect(Collectors.toList())
                //将所有的List<List<OrderItem>>都整合到一起List<OrderItem>
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        //封装订单号及商品名称
        orderItemList.forEach(
                orderItem -> {
                    builder.append(orderItem.getSkuName() + " ");
                    orderItem.setOrderNumber(orderNum);
                }
        );
        //批量新增orderItemList
        orderItemService.saveBatch(orderItemList);

        //新增订单数据
        orderService.save(
                //将orderVo中的店铺对应的订单列表数据，批量创建
                Order.builder()
                        .prodName(builder.toString())
                        .userId(orderVo.getUserAddr().getUserId())
                        .orderNumber(orderNum)
                        .total(orderVo.getTotal())
                        .actualTotal(orderVo.getActualTotal())
                        .payType(0)
                        .remarks(orderVo.getRemarks())
                        .status(1)
                        .freightAmount(orderVo.getTransfee())
                        .addrOrderId(orderVo.getUserAddr().getAddrId())
                        .productNums(orderVo.getTotalCount())
                        .reduceAmount(BigDecimal.ZERO)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .isPayed(0)
                        .deleteStatus(0)
                        .refundSts(0)
                        .build()
        );
        return builder.toString();
    }

    private void deductEsStock(List<ProdEsCount> countList) {
        //发送消息到消息队列中(快速导入)
        rabbitTemplate.convertAndSend(
                QueueConstants.ES_CHANGE_QUEUE,
                JSON.toJSONString(countList)
        );
    }

    private void deductMySqlStock(List<ProdEsCount> countList) {
        //发送远程调用，扣减es库存信息
        productServiceFeign.deductMySqlStock(countList);
    }

    private List<ProdEsCount> clearCart(OrderVo orderVo) {

        //String userId = getWxUserId();

        //将店铺和所有的商品集合，进行转换获取我们的转换后的数据
        List<ProdEsCount> countList = orderVo.getShopCartOrders()
                .stream()
                //将所有的店铺集合收集到一起
                .map(ShopOrder::getShopCartItemDiscounts)
                //收集后的数据List<List<OrderItem>>
                .collect(Collectors.toList())
                //将所有的List<List<OrderItem>>都整合到一起List<OrderItem>
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
                .stream()
                .map(
                        orderItem -> ProdEsCount.builder()
                                .count(orderItem.getProdCount().longValue())
                                .prodId(orderItem.getProdId())
                                .skuId(orderItem.getSkuId())
                                .build()
                )
                .collect(Collectors.toList());

        countList.forEach(
                count ->  count.setCount(
                        count.getCount() * -1
                )
        );

        //发送远程请求 ，清空购物车列表数据
        //收集skuIds
        //List<Long> skuIds = countList.stream().map(ProdEsCount::getSkuId).collect(Collectors.toList());

        //远程调用，清理购物车列表数据
        //cartServiceFeign.clearCart(userId,skuIds);

        return countList;
    }

    //雪花算法生成分布式ID
    private String createOrderNum() {
        return snowflake.nextIdStr();
    }

}
