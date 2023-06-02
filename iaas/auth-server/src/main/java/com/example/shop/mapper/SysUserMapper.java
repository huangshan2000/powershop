package com.example.shop.mapper;

import com.example.shop.domain.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【sys_user(系统用户)】的数据库操作Mapper
* @createDate 2023-05-25 15:44:49
* @Entity com.example.shop.domain.SysUser
*/
public interface SysUserMapper extends BaseMapper<SysUser> {

    List<String> selectPermsList(Long userId);
}




