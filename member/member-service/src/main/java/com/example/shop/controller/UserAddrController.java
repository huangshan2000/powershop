package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shop.base.BaseMember;
import com.example.shop.domain.User;
import com.example.shop.domain.UserAddr;
import com.example.shop.entity.R;
import com.example.shop.service.UserAddrService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author: William
 * @date: 2023-06-02 17:31
 **/
@RestController
@RequestMapping("/p/address")
@RequiredArgsConstructor
public class UserAddrController extends BaseMember {

    private final UserAddrService userAddrService;

    @GetMapping("/list")
    public R<List<UserAddr>> list() {
        return ok(
                userAddrService.list(
                        new LambdaQueryWrapper<UserAddr>()
                                .eq(UserAddr::getUserId, getWxUserId())
                                .eq(UserAddr::getStatus, 1)
                )
        );
    }

    @PutMapping("defaultAddr/{addrId}")
    public R<Boolean> defaultAddr(@PathVariable("addrId") Long addrId) {
        //根据addrId查询地址信息
        UserAddr userAddr = userAddrService.getById(addrId);

        if (userAddr.getCommonAddr().equals(1))
            //用户点错了，误操作
            return ok(true);

        //查询旧的地址信息
        UserAddr oldAddr = userAddrService.getOne(
                new LambdaQueryWrapper<UserAddr>()
                        .eq(UserAddr::getUserId, getWxUserId())
                        .eq(UserAddr::getCommonAddr, 1)
        );

        //将旧的地址信息的默认收货标识，修改为0
        boolean oldAddrFlag = userAddrService.updateById(
                oldAddr.setCommonAddr(0).setUpdateTime(new Date())
        );

        if (!oldAddrFlag)
            throw new RuntimeException("地址更新失败");

        //将新的地址信息更新
        boolean userAddrFlag = userAddrService.updateById(
                userAddr.setCommonAddr(1).setUpdateTime(new Date())
        );

        if (!userAddrFlag)
            throw new RuntimeException("地址更新失败");

        return ok(
                userAddrFlag && oldAddrFlag
        );
    }

    @PostMapping("/addAddr")
    public R<Boolean> addAddr(@RequestBody UserAddr userAddr) {
        return ok(
                userAddrService.save(
                        userAddr.setUserId(getWxUserId())
                                .setStatus(1)
                                .setCommonAddr(
                                        //查询是否已经存在默认收货地址
                                        userAddrService.count(
                                                new LambdaQueryWrapper<UserAddr>()
                                                        .eq(UserAddr::getUserId, getWxUserId())
                                                        .eq(UserAddr::getCommonAddr, 1)
                                        ) > 0 ? 0 : 1
                                )
                                .setCreateTime(new Date())
                                .setVersion(1)
                                .setUpdateTime(new Date())
                )
        );
    }

    @GetMapping("/default")
    public UserAddr defaultUserAddr(@RequestParam("userId") String userId) {
        return userAddrService.getOne(
                new LambdaQueryWrapper<UserAddr>()
                        .eq(StringUtils.isNotBlank(userId),UserAddr::getUserId,userId)
                        .eq(UserAddr::getCommonAddr,1)
        );
    }
}
