package com.example.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.UserAddrOrder;
import com.example.shop.service.UserAddrOrderService;
import com.example.shop.mapper.UserAddrOrderMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【user_addr_order(用户订单配送地址)】的数据库操作Service实现
* @createDate 2023-06-02 17:10:15
*/
@Service
public class UserAddrOrderServiceImpl extends ServiceImpl<UserAddrOrderMapper, UserAddrOrder>
    implements UserAddrOrderService{

}




