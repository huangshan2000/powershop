package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shop.base.BaseStore;
import com.example.shop.domain.Notice;
import com.example.shop.entity.R;
import com.example.shop.service.NoticeService;
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
@RequestMapping("/shop/notice")
@RequiredArgsConstructor
public class NoticeController extends BaseStore {

    private final NoticeService noticeService;

    @GetMapping("/page")
    public R<Page<Notice>> page(Page<Notice> page,Notice notice) {
        return ok(
                noticeService.page(
                        page,
                        new LambdaQueryWrapper<Notice>()
                                .eq(ObjectUtils.isNotEmpty(notice.getStatus()),Notice::getStatus,notice.getStatus())
                                .eq(ObjectUtils.isNotEmpty(notice.getIsTop()),Notice::getIsTop,notice.getIsTop())
                                .like(StringUtils.isNotBlank(notice.getTitle()),Notice::getTitle,notice.getTitle())
                )
        );
    }

    @PostMapping
    public R<Boolean> saveNotice(@RequestBody Notice notice) {

        return ok(
                noticeService.save(
                        notice.setShopId(1L)
                                .setPublishTime(new Date())
                                .setUpdateTime(new Date())
                )
        );
    }
}
