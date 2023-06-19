package com.example.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.Order;
import com.example.shop.service.OrderService;
import com.example.shop.mapper.OrderMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【order(订单表)】的数据库操作Service实现
* @createDate 2023-06-19 20:48:14
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{

}




