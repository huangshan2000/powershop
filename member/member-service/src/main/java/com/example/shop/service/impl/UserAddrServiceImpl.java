package com.example.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.UserAddr;
import com.example.shop.service.UserAddrService;
import com.example.shop.mapper.UserAddrMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【user_addr(用户配送地址)】的数据库操作Service实现
* @createDate 2023-06-02 17:10:15
*/
@Service
public class UserAddrServiceImpl extends ServiceImpl<UserAddrMapper, UserAddr>
    implements UserAddrService{

}




