package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shop.base.BaseProduct;
import com.example.shop.domain.Category;
import com.example.shop.entity.R;
import com.example.shop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author: William
 * @date: 2023-05-31 13:37
 **/
//全局搜索  ctrl+shift+r
@RestController
@RequestMapping("/prod/category")
@RequiredArgsConstructor
public class CategoryController extends BaseProduct {

    private final CategoryService categoryService;

    @GetMapping("/table")
    public R<List<Category>> table(@RequestParam(value = "parentId", required = false) Long parentId) {
        return ok(
                categoryService.list(
                        new LambdaQueryWrapper<Category>()
                                .eq(Category::getStatus, 1)
                                .eq(ObjectUtils.isNotEmpty(parentId), Category::getParentId, parentId)
                                .orderByDesc(Category::getSeq)
                )
        );
    }

    @GetMapping("/listCategory")
    public R<List<Category>> listCategory() {

        return table(0L);
    }

    @PostMapping
    public R<Boolean> saveCategory(@RequestBody Category category) {
        return ok(
                categoryService.save(
                        category.setShopId(1L)
                                .setRecTime(new Date())
                                .setUpdateTime(new Date())
                                //通过parentId判断商品类别
                                .setGrade(
                                        category.getParentId().equals(0) ? 1 : 2
                                )
                )
        );
    }

}
