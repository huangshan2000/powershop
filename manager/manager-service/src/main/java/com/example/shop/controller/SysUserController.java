package com.example.shop.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.shop.domain.SysUser;
import com.example.shop.dto.SysUserDTO;
import com.example.shop.entity.R;
import com.example.shop.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: William
 * @date: 2023-05-29 12:47
 **/
@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class SysUserController {

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
        BeanUtil.copyProperties(sysUser, sysUserDTO,false);
        return new R<SysUserDTO>(
                sysUserDTO
        );
    }
}
