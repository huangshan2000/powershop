package com.example.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.OrderSettlement;
import com.example.shop.service.OrderSettlementService;
import com.example.shop.mapper.OrderSettlementMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【order_settlement】的数据库操作Service实现
* @createDate 2023-06-19 20:48:14
*/
@Service
public class OrderSettlementServiceImpl extends ServiceImpl<OrderSettlementMapper, OrderSettlement>
    implements OrderSettlementService{

}




