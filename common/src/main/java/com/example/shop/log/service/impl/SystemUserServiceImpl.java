package com.example.shop.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.log.domain.SystemUser;
import com.example.shop.log.mapper.SystemUserMapper;
import com.example.shop.log.service.SystemUserService;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【sys_user(系统用户)】的数据库操作Service实现
* @createDate 2023-05-30 14:55:01
*/
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser>
    implements SystemUserService{

}




