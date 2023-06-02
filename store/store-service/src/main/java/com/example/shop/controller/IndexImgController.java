package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shop.base.BaseStore;
import com.example.shop.domain.IndexImg;
import com.example.shop.entity.R;
import com.example.shop.service.IndexImgService;
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
@RequestMapping("/admin/indexImg")
@RequiredArgsConstructor
public class IndexImgController extends BaseStore {

    private final IndexImgService indexImgService;

    @GetMapping("/page")
    public R<Page<IndexImg>> page(Page<IndexImg> page,IndexImg indexImg) {
        return ok(
                indexImgService.page(
                        page,
                        new LambdaQueryWrapper<IndexImg>()
                                .eq(ObjectUtils.isNotEmpty(indexImg.getStatus()),IndexImg::getStatus,indexImg.getStatus())
                )
        );
    }

    @PostMapping
    public R<Boolean> saveIndexImg(@RequestBody IndexImg indexImg) {

        return ok(
                indexImgService.save(
                        indexImg.setShopId(1L)
                                .setUploadTime(new Date())
                )
        );
    }
}
