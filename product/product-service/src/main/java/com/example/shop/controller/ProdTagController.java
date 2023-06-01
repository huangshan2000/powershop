package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shop.base.BaseProduct;
import com.example.shop.domain.ProdTag;
import com.example.shop.entity.R;
import com.example.shop.service.ProdTagService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;

import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author: William
 * @date: 2023-05-31 14:22
 **/

@RestController
@RequestMapping("/prod/prodTag")
@RequiredArgsConstructor
public class ProdTagController extends BaseProduct {

    private final ProdTagService prodTagService;

    @GetMapping("/page")
    //多条件使用类ProdTag接收，但条件使用属性直接接收即可
    public R<Page<ProdTag>> page(Page<ProdTag> page, ProdTag prodTag) {
        return ok(
                prodTagService.page(
                        page,
                        new LambdaQueryWrapper<ProdTag>()
                                .eq(ObjectUtils.isNotEmpty(prodTag.getStatus()), ProdTag::getStatus, prodTag.getStatus())
                                .like(
                                        StringUtils.isNotBlank(prodTag.getTitle()), ProdTag::getTitle, prodTag.getTitle()
                                )
                )
        );
    }

    @PostMapping
    public R<Boolean> saveProdTage(@RequestBody ProdTag prodTag) {
        return ok(
                prodTagService.save(
                        prodTag.setId(0L)
                                .setCreateTime(new Date())
                                .setUpdateTime(new Date())
                                .setShopId(1L)
                )
        );
    }

    @GetMapping("/listTagList")
    public R<List<ProdTag>> listTagList() {
        return ok(
                prodTagService.list()
        );
    }
}
