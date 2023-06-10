package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shop.base.BaseCart;
import com.example.shop.domain.*;
import com.example.shop.entity.R;
import com.example.shop.feign.ProductServiceFeign;
import com.example.shop.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: William
 * @date: 2023-06-09 23:32
 **/
@RestController
@RequestMapping("/p/shopCart")
@RequiredArgsConstructor
public class BasketController extends BaseCart {

    private final BasketService basketService;

    private final ProductServiceFeign productServiceFeign;

    @GetMapping("/prodCount")
    public R<Integer> prodCount() {
        return ok(
                basketService.count(
                        new LambdaQueryWrapper<Basket>()
                                .eq(Basket::getUserId, getWxUserId())
                )
        );
    }

    /**
     * 将List<Basket>购物车数据，转换为Cart对象(页面显示)
     */
    @GetMapping("/info")
    public R<Cart> info(@RequestParam(value = "basketIds", required = false) List<Long> basketIds) {
        //创建封装的返回值对象
        Cart cart = new Cart();

        //封装总金额VO对象
        CartMoney cartMoney = new CartMoney();

        cart.setCartMoney(cartMoney);

        //查询购物车的列表数据
        List<Basket> basketList = basketService.list(
                new LambdaQueryWrapper<Basket>()
                        .eq(Basket::getUserId, getWxUserId())
                        .in(!CollectionUtils.isEmpty(basketIds), Basket::getBasketId, basketIds)
        );

        if (CollectionUtils.isEmpty(basketList))
            return ok(cart);

        //收集skuIds，远程调用查询skuList数据
        List<Long> skuIds = basketList.stream().map(Basket::getSkuId).collect(Collectors.toList());

        List<Sku> skuList = productServiceFeign.skuList(skuIds);

        if (CollectionUtils.isEmpty(skuList))
            return ok(cart);

        //我们创建cart对象中的店铺集合、店铺集合中封装店铺对象、店铺对象中封装商品详情列表数据
        List<ShopCart> shopCarts = new ArrayList<>();
        cart.setShopCarts(shopCarts);

        //将购物车列表数据，按照店铺的ID，进行分别的收取数据
        Map<Long, List<Basket>> shopCartsMap = basketList.stream().collect(
                Collectors.groupingBy(Basket::getShopId)
        );
        /*
            原本的数据
                [
                    {
                        basketId:1,shopId:1,prodId:95,skuId:427...
                    },
                    {
                        basketId:2,shopId:1,prodId:95,skuId:428...
                    },
                    {
                        basketId:3,shopId:2,prodId:95,skuId:429...
                    }
                ]
            收集后的数据：
                {
                    1(shopId):[
                        {
                            basketId:1,shopId:1,prodId:95,skuId:427...
                        },
                        {
                            basketId:2,shopId:1,prodId:95,skuId:428...
                        }
                    ],
                    2(shopId):[
                        {
                            basketId:3,shopId:2,prodId:95,skuId:429...
                        }
                    ]
                }
         */

        //封装最终金额、总金额、优惠金额
        List<BigDecimal> finalMoneyList = new ArrayList<>();
        List<BigDecimal> totalMoneyList = new ArrayList<>();
        List<BigDecimal> subtractMoneyList = new ArrayList<>();
        List<BigDecimal> transMoneyList = new ArrayList<>();

        //遍历收集后的map集合，每个kv就是每个店铺的数据
        shopCartsMap.forEach(
                (shopId, baskets) -> {
                    //创建并封装店铺对象
                    ShopCart shopCart = new ShopCart();
                    shopCarts.add(shopCart);

                    //创建并封装店铺中的商品列表数据
                    List<CartItem> cartItems = new ArrayList<>();
                    shopCart.setShopCartItems(cartItems);

                    //创建统计运费的集合
                    List<BigDecimal> shopPriceList = new ArrayList<>();

                    //遍历购物车的数据，封装成CartItem
                    baskets.forEach(
                            basket -> {

                                //将skuList中的对应sku对象过滤出来
                                List<Sku> skus = skuList.stream().filter(sku -> sku.getSkuId().equals(basket.getSkuId())).collect(Collectors.toList());
                                if (!CollectionUtils.isEmpty(skus)) {
                                    //获取sku对象
                                    Sku sku = skus.get(0);


                                    //封装
                                    cartItems.add(
                                            CartItem.builder()
                                                    //购物车Id
                                                    .basketId(basket.getBasketId())
                                                    //前端是否选中的标记
                                                    .checked(true)
                                                    .prodId(basket.getProdId())
                                                    .skuId(basket.getSkuId())
                                                    .basketCount(basket.getBasketCount())
                                                    .prodName(sku.getProdName())
                                                    .skuName(sku.getSkuName())
                                                    .pic(sku.getPic())
                                                    .price(sku.getPrice().toPlainString())
                                                    .build()
                                    );

                                    //将每个商品的总金额封装到集合中
                                    shopPriceList.add(
                                            sku.getPrice().multiply(new BigDecimal(basket.getBasketCount()))
                                    );

                                }
                            }
                    );

                    //计算店铺运费
                    //当店铺消费满99元，包含99元，包邮，不满则运费6元
                    BigDecimal bigDecimal = shopPriceList.stream().reduce(BigDecimal::add).get();

                    //计算当前店铺的运费
                    if (bigDecimal.compareTo(new BigDecimal(99)) < 0)
                        shopCart.setYunfei(new BigDecimal(6));

                    //封装总金额、最终金额...
                    totalMoneyList.add(bigDecimal);
                    finalMoneyList.add(bigDecimal);
                    subtractMoneyList.add(shopCart.getShopReduce());
                    transMoneyList.add(shopCart.getYunfei());
                }
        );

        //计算总金额、最终金额...
        BigDecimal totalMoney = totalMoneyList.stream().reduce(BigDecimal::add).get();
        BigDecimal finalMoney = finalMoneyList.stream().reduce(BigDecimal::add).get();
        BigDecimal subtractMoney = subtractMoneyList.stream().reduce(BigDecimal::add).get();
        BigDecimal transMoney = transMoneyList.stream().reduce(BigDecimal::add).get();

        //封装总金额、最终金额...
        cartMoney.setSubtractMoney(subtractMoney);
        cartMoney.setTransMoney(transMoney);
        cartMoney.setTotalMoney(totalMoney);
        cartMoney.setFinalMoney(
                //finalMoney = finalMoney + transMoney + subtractMoney(+/-)
                finalMoney.add(transMoney).add(subtractMoney)
        );
        return ok(
                cart
        );
    }

    @GetMapping("/totalPay")
    public R<CartMoney> totalPay(@RequestParam List<Long> basketIds) {
        return ok(
                info(basketIds).getBody().getCartMoney()
        );
    }
}
