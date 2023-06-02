package com.example.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.SysRole;
import com.example.shop.domain.SysRoleMenu;
import com.example.shop.service.SysRoleMenuService;
import com.example.shop.service.SysRoleService;
import com.example.shop.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author Lenovo
* @description 针对表【sys_role(角色)】的数据库操作Service实现
* @createDate 2023-05-28 11:46:00
*/
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
    implements SysRoleService{

    private final SysRoleMapper sysRoleMapper;

    private final SysRoleMenuService sysRoleMenuService;

    @Override
    @Transactional
    public boolean save(SysRole entity) {
        //新增角色信息
        int flag = sysRoleMapper.insert(entity);

        if (flag <= 0)
            throw new RuntimeException("新增角色失败");

        //批量新增角色和菜单的关联关系
        List<SysRoleMenu> sysRoleMenuList = entity.getMenuIdList().stream()
                .map(
                        menuId -> SysRoleMenu.builder()
                                .roleId(entity.getRoleId())
                                .menuId(menuId)
                                .build()
                ).collect(Collectors.toList());

        //批量新增角色和菜单的关联关系
        boolean sysRoleMenuFlag = sysRoleMenuService.saveBatch(sysRoleMenuList);

        if (!sysRoleMenuFlag)
            throw new RuntimeException("新增角色和菜单关联关系失败");

        return flag  > 0 && sysRoleMenuFlag;
    }
}




