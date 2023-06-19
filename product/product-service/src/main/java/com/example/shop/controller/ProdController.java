package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shop.base.BaseProduct;
import com.example.shop.domain.Prod;
import com.example.shop.domain.ProdEsCount;
import com.example.shop.domain.Sku;
import com.example.shop.entity.R;
import com.example.shop.service.ProdService;
import com.example.shop.service.SkuService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: William
 * @date: 2023-06-01 10:04
 **/
@RestController
@RequestMapping("/prod/prod")
@RequiredArgsConstructor
public class ProdController extends BaseProduct {

    private final ProdService prodService;

    private final SkuService skuService;

    @GetMapping("/page")
    public R<Page<Prod>> page(Page<Prod> page, Prod prod) {
        return ok(
                prodService.page(
                        page,
                        new LambdaQueryWrapper<Prod>()
                                .eq(ObjectUtils.isNotEmpty(prod.getStatus()), Prod::getStatus, prod.getStatus())
                                .like(StringUtils.isNotBlank(prod.getProdName()), Prod::getProdName, prod.getProdName())
                )
        );
    }

    /*
        deliveryModeVo: {hasShopDelivery: false, hasUserPickUp: true}
        skuList: [{price: "110", oriPrice: "10", stocks: "10", properties: "壁纸类型:标清", skuName: "标清 ",…},…]
        tagList: [5, 1, 2, 3, 4]
     */
    @PostMapping
    public R<Boolean> saveProd(@RequestBody Prod prod) {
        return ok(
                prodService.save(prod)
        );
    }

    //----------------远程调用-------------------
    @GetMapping("/list")
    public List<Prod> list(@RequestParam("ids") List<Long> ids) {
        return prodService.list(
                new LambdaQueryWrapper<Prod>()
                        .in(Prod::getProdId, ids)
        );
    }

    @GetMapping("/sku/list")
    public List<Sku> skuList(@RequestParam("ids") List<Long> ids) {
        return skuService.listByIds(ids);
    }

    /**
     * 扣减库存信息
     * prod和sku表的库存信息
     *
     * @param countList
     * @return
     */
    @PutMapping("/prod/prod/deductMySqlStock")
    boolean deductMySqlStock(List<ProdEsCount> countList){
        //方式1：可以for循环List集合，遍历进行扣减库存


        //方式2：也可以根据prodId将同一商品的数据整理出来进行批量操作
        Map<Long, List<ProdEsCount>> prodMap = countList.stream().collect(
                Collectors.groupingBy(ProdEsCount::getProdId)
        );

        //获取prodList集合和skuList集合
        List<Long> prodIds = countList.stream().map(ProdEsCount::getProdId).collect(Collectors.toList());
        List<Long> skuIds = countList.stream().map(ProdEsCount::getSkuId).collect(Collectors.toList());

        //查询数据
        List<Prod> prodList = prodService.listByIds(prodIds);
        List<Sku> skuList = skuService.listByIds(skuIds);

        prodMap.forEach(
                (prodId,counts) -> {
                    //计算prod扣减的总库存
                    Long totalStock = counts.stream().map(ProdEsCount::getCount).collect(Collectors.toList())
                            .stream().reduce(Long::sum).get();

                    //过滤出当前商品的数据
                    List<Prod> prods = prodList.stream().filter(prod -> prod.getProdId().equals(prodId)).collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(prods)) {
                        //获取prod对象
                        Prod prod = prods.get(0);

                        //扣减prod
                        prod.setTotalStocks(new BigDecimal(prod.getTotalStocks()).add(new BigDecimal(totalStock)).intValue());
                    }

                    //遍历counts，扣减每个sku数据
                    counts.forEach(
                            count -> {
                                //过滤出当前的sku对象
                                List<Sku> skus = skuList.stream().filter(sku -> sku.getSkuId().equals(count.getSkuId())).collect(Collectors.toList());

                                if (!CollectionUtils.isEmpty(skus)) {
                                    Sku sku = skus.get(0);

                                    //扣减库存
                                    sku.setStocks(new BigDecimal(sku.getStocks()).add(new BigDecimal(count.getCount())).intValue())
                                            .setActualStocks(new BigDecimal(sku.getActualStocks()).add(new BigDecimal(count.getCount())).intValue());


                                }
                            }
                    );

                }
        );

        //批量更新操作
        boolean prodFlag = prodService.updateBatchById(prodList);

        boolean skuFlag = skuService.updateBatchById(skuList);

        return prodFlag && skuFlag;
    }
}
