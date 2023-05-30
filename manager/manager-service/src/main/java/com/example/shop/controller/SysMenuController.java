package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shop.domain.SysMenu;
import com.example.shop.dto.MenuAndAuth;
import com.example.shop.entity.R;
import com.example.shop.log.anno.Log;
import com.example.shop.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: William
 * @date: 2023-05-28 18:45
 **/
@RestController
@RequestMapping("/sys/menu")
//通过构造器的方式注入属性信息
@RequiredArgsConstructor
public class SysMenuController {

    //这种注入Bean的方式，虽然可以使用，但是SpringBoot不太推荐这样使用
    //因为这种注入方式是通过反射的机制进行加载的，效率较慢
    //@Autowired
    //private SysMenuService sysMenuService;

    //推荐使用构造器方式通过容器进行注入
    private final SysMenuService sysMenuService;

    //public SysMenuController(SysMenuService sysMenuService) {
    //    this.sysMenuService = sysMenuService;
    //}


    /**
     * 登录后，获取菜单列表及权限列表
     *
     * @return
     */
    @GetMapping("/nav")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public R<MenuAndAuth> nav() {
        /**
         * Authentication ，主要讲的是和登录相关的内容。
         *      Authentication：未登录的时候调用需要登录的接口，一般使用 401 Unauthorized。
         * Authorization ，主要讲的是权限相关的内容（注意，Gates 和 Policies 都必须是在用户登录后才有意义，如果没有登录态，直接返回 false）。
         *      Authorization：登录后请求没有权限的接口，一般使用 403 Forbidden。
         */
        //从Security中取权限列表信息
        Authentication authentication = SecurityContextHolder
                //通过Security上下文对象
                .getContext()
                //获取认证的用户信息
                .getAuthentication();

        List<String> authorities =
                //获取当前用户的权限列表数据Collection<GrantedAuthority>
                authentication.getAuthorities()
                        //将集合通过Stream流转换为List<String>
                        .stream()
                        //将权限信息进行转换并提取
                        .map(
                                authority -> authority.getAuthority()
                        )
                        //转换为新的List集合
                        .collect(Collectors.toList());

        //根据用户id查询用户的菜单列表信息
        List<SysMenu> sysMenuList = translateMenuAndSubMenuList(sysMenuService.selectMenuList(authentication.getName()), 0L);

        return new R<MenuAndAuth>(
                MenuAndAuth.builder()
                        .authorities(authorities)
                        .menuList(sysMenuList)
                        .build()
        );
    }

    /**
     * 将所有菜单列表数据(包含一级菜单和二级菜单)递归封装成一级菜单集合(封装二级菜单集合)
     */
    private List<SysMenu> translateMenuAndSubMenuList(List<SysMenu> rootMenuList, long parentId) {

        //将一级菜单收集起来
        List<SysMenu> oneLevelMenuList = rootMenuList.stream()
                //条件过滤，当parentId等于0时，代表是一级菜单的数据
                .filter(
                        menu -> menu.getParentId().equals(parentId)
                )
                //封装新的集合
                .collect(Collectors.toList());

        //遍历一级菜单，递归封装每个一级菜单的子菜单，如果后续有三级菜单，那么代码不用变动，递归会自动迭代封装
        oneLevelMenuList.forEach(
                rootMenu -> {
                    rootMenu.setList(
                            translateMenuAndSubMenuList(rootMenuList, rootMenu.getMenuId())
                    );
                }
        );
        return oneLevelMenuList;
    }

    /**
     *  如果是list请求 ，那么可以查询type为0或为1的菜单列表数据
     *  如果是table请求，那么查询所有菜单列表数据
     * @return
     */
    @Log(operation = "查询菜单列表信息")
    @GetMapping({"/table","/list"})
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public R<List<SysMenu>> table() {
        //查询菜单列表数据
        return new R<List<SysMenu>>(
                sysMenuService.list(
                        new LambdaQueryWrapper<SysMenu>()
                                .orderByDesc(SysMenu::getOrderNum)
                        )
                );
    }

    @PostMapping
    public R<Boolean> saveMenu(@RequestBody SysMenu sysMenu) {
        return new R<Boolean>(
                sysMenuService.save(sysMenu)
        );
    }
}
