package com.example.shop.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shop.base.BaseManager;
import com.example.shop.domain.SysRole;
import com.example.shop.domain.SysUser;
import com.example.shop.dto.SysUserDTO;
import com.example.shop.entity.R;
import com.example.shop.log.anno.Log;
import com.example.shop.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author: William
 * @date: 2023-05-29 12:47
 **/
@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class SysUserController extends BaseManager {

    private final SysUserService sysUserService;

    @GetMapping("/info")
    public R<SysUserDTO> info() {
        //根据用户id查询用户信息
        SysUser sysUser = sysUserService.getById(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        //通过DTO过滤的敏感数据，比如：password
        SysUserDTO sysUserDTO = new SysUserDTO();
        //返回结果集
        BeanUtil.copyProperties(sysUser, sysUserDTO, false);
        return new R<SysUserDTO>(
                sysUserDTO
        );
    }

    @Log(operation = "查询用户列表信息")
    @GetMapping("/page")
    public R<Page> page(Page page, @RequestParam(value = "username", required = false) String username) {
        return ok(
                sysUserService.page(
                        page,
                        new LambdaQueryWrapper<SysUser>()
                                .eq(SysUser::getStatus, 1)
                                .like(StringUtils.isNotBlank(username), SysUser::getUsername, username)
                )
        );
    }

    @PostMapping
    public R<Boolean> saveUser(@RequestBody SysUser sysUser) {
        return ok(
                sysUserService.save(
                        sysUser.setShopId(1L)
                                .setCreateUserId(getUserId())
                                .setCreateTime(new Date())
                )
        );

    }


}
