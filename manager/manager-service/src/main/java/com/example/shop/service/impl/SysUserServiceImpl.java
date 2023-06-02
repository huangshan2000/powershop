package com.example.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.SysUser;
import com.example.shop.domain.SysUserRole;
import com.example.shop.service.SysUserRoleService;
import com.example.shop.service.SysUserService;
import com.example.shop.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.stream.Collectors;

/**
* @author Lenovo
* @description 针对表【sys_user(系统用户)】的数据库操作Service实现
* @createDate 2023-05-28 11:46:00
*/
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{

    private final SysUserRoleService sysUserRoleService;

    private final SysUserMapper sysUserMapper;

    @Override
    public boolean save(SysUser entity) {
        //新增用户信息
        int flagUser = sysUserMapper.insert(entity);
        if (flagUser <= 0)
            throw new RuntimeException("新增用户失败");

        //批量新增用户和角色的关联关系
        boolean flagSysUserRole = sysUserRoleService.saveBatch(
                entity.getRoleIdList().stream()
                        .map(
                                roleId -> SysUserRole.builder()
                                        .userId(entity.getUserId())
                                        .roleId(roleId)
                                        .build()
                        ).collect(Collectors.toList())
        );

        if(!flagSysUserRole)
            throw new RuntimeException("新增用户和角色的关联关系失败");

        return flagUser > 0 && flagSysUserRole;
    }
}




