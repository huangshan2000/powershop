package com.example.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.Basket;
import com.example.shop.service.BasketService;
import com.example.shop.mapper.BasketMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【basket(购物车)】的数据库操作Service实现
* @createDate 2023-06-09 22:44:16
*/
@Service
public class BasketServiceImpl extends ServiceImpl<BasketMapper, Basket>
    implements BasketService{

}




