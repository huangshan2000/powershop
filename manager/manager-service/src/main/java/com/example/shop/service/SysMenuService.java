package com.example.shop.service;

import com.example.shop.domain.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【sys_menu(菜单管理)】的数据库操作Service
* @createDate 2023-05-28 11:46:00
*/
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> selectMenuList(String userId);
}
