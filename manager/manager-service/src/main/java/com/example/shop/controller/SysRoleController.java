package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shop.base.BaseManager;
import com.example.shop.domain.SysRole;
import com.example.shop.entity.R;
import com.example.shop.log.anno.Log;
import com.example.shop.service.SysRoleService;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author: William
 * @date: 2023-05-30 09:22
 **/

@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
public class SysRoleController extends BaseManager {

    private final SysRoleService sysRoleService;

    @Log(operation = "查询角色列表信息")
    @GetMapping("/page")
    public R<Page<SysRole>> page(Page<SysRole> page, @RequestParam(value = "roleName",required = false) String roleName) {
            return new R<>(
                    sysRoleService.page(
                            page,
                            new LambdaQueryWrapper<SysRole>()
                                    .like(StringUtils.isNotBlank(roleName), SysRole::getRoleName, roleName)
                    )
            );
    }

    @PostMapping
    public R<Boolean> saveRole(@RequestBody SysRole sysRole) {
        return new R<>(sysRoleService.save(
                sysRole.setCreateUserId(getUserId())
                        .setCreateTime(new Date())
        ));
    }

    @GetMapping("/list")
    public R<List<SysRole>> list() {
        return ok(
                sysRoleService.list()
        );
    }

}
