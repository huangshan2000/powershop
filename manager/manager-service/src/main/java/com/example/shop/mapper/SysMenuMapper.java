package com.example.shop.mapper;

import com.example.shop.domain.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【sys_menu(菜单管理)】的数据库操作Mapper
* @createDate 2023-05-28 11:46:00
* @Entity com.example.shop.domain.SysMenu
*/
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> selectMenuList(String userId);
}




