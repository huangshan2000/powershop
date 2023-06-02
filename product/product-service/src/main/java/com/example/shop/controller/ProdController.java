package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shop.base.BaseProduct;
import com.example.shop.domain.Prod;
import com.example.shop.entity.R;
import com.example.shop.service.ProdService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: William
 * @date: 2023-06-01 10:04
 **/
@RestController
@RequestMapping("/prod/prod")
@RequiredArgsConstructor
public class ProdController extends BaseProduct {

    private final ProdService prodService;

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
                        .in(Prod::getProdId,ids)
        );
    }
}
