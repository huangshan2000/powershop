package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shop.base.BaseStore;
import com.example.shop.domain.HotSearch;
import com.example.shop.entity.R;
import com.example.shop.service.HotSearchService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author: William
 * @date: 2023-06-02 11:12
 **/
@RestController
@RequestMapping("/admin/hotSearch")
@RequiredArgsConstructor
public class HotSearchController extends BaseStore {

    private final HotSearchService hotSearchService;

    @GetMapping("/page")
    public R<Page<HotSearch>> page(Page<HotSearch> page,HotSearch hotSearch) {
        return ok(
                hotSearchService.page(
                        page,
                        new LambdaQueryWrapper<HotSearch>()
                                .eq(ObjectUtils.isNotEmpty(hotSearch.getStatus()),HotSearch::getStatus,hotSearch.getStatus())
                                .like(StringUtils.isNotBlank(hotSearch.getTitle()),HotSearch::getTitle,hotSearch.getTitle())
                                .like(StringUtils.isNotBlank(hotSearch.getContent()),HotSearch::getContent,hotSearch.getContent())
                )
        );
    }

    @PostMapping
    public R<Boolean> saveHotSearch(@RequestBody HotSearch hotSearch) {

        return ok(
                hotSearchService.save(
                        hotSearch.setShopId(1L)
                                .setRecDate(new Date())
                )
        );
    }
}
