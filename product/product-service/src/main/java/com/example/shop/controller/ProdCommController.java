package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shop.base.BaseProduct;
import com.example.shop.domain.ProdComm;
import com.example.shop.entity.R;
import com.example.shop.service.ProdCommService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

/**
 * @author: William
 * @date: 2023-05-31 16:54
 **/

@RestController
@RequestMapping("/prod/prodComm")
@RequiredArgsConstructor
public class ProdCommController extends BaseProduct {

    /**
     *  如果可以添加字段，那么实现起来比较容易
     *  如果不能够添加字段，那么实现起来非常麻烦，而且查询出来的数据不一定准确
     *      因为当前商品的评论数据较多，而且分页显示，每页显示10条数据
     *      1. 根据商品名称，模糊查询商品数据
     *      2. 再根据商品数据的ids，分页查询出第一页的数据
     */
    //如果可以添加字段，那么实现起来比较容易
    private final ProdCommService prodCommService;

    @GetMapping("/page")
    public R<Page<ProdComm>> page(Page<ProdComm> page,
                                  @RequestParam(value = "prodName",required = false) String prodName,
                                  @RequestParam(value = "status" ,required = false) Integer status) {
        return ok(
                prodCommService.page(
                        page,
                        new LambdaQueryWrapper<ProdComm>()
                                .eq(ObjectUtils.isNotEmpty(status),ProdComm::getStatus,1)
                                .like(StringUtils.isNotBlank(prodName),ProdComm::getProdName,prodName)
                        )
        );
    }
}
