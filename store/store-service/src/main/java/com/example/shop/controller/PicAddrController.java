package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shop.base.BaseStore;
import com.example.shop.domain.Area;
import com.example.shop.domain.PickAddr;
import com.example.shop.entity.R;
import com.example.shop.service.AreaService;
import com.example.shop.service.PickAddrService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author: William
 * @date: 2023-06-02 11:12
 **/
@RestController
@RequestMapping("shop/pickAddr")
@RequiredArgsConstructor
public class PicAddrController extends BaseStore {

    private final PickAddrService pickAddrService;

    private final AreaService areaService;

    @GetMapping("/page")
    public R<Page<PickAddr>> page(Page<PickAddr> page,PickAddr pickAddr) {
        return ok(
                pickAddrService.page(
                        page,
                        new LambdaQueryWrapper<PickAddr>()
                                .like(StringUtils.isNotBlank(pickAddr.getAddrName()),PickAddr::getAddrName,pickAddr.getAddrName())
                )
        );
    }

    @PostMapping
    public R<Boolean> savePickAddr(@RequestBody PickAddr pickAddr) {

        return ok(
                pickAddrService.save(
                        pickAddr.setShopId(1L)
                )
        );
    }


}
