package com.example.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.OrderItem;
import com.example.shop.service.OrderItemService;
import com.example.shop.mapper.OrderItemMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【order_item(订单项)】的数据库操作Service实现
* @createDate 2023-06-19 20:48:14
*/
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem>
    implements OrderItemService{

}




