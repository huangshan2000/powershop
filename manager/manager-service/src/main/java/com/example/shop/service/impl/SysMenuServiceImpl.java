package com.example.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.SysMenu;
import com.example.shop.service.SysMenuService;
import com.example.shop.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【sys_menu(菜单管理)】的数据库操作Service实现
* @createDate 2023-05-28 11:46:00
*/
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
    implements SysMenuService{

    private final SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenu> selectMenuList(String userId) {
        return sysMenuMapper.selectMenuList(userId);
    }
}




