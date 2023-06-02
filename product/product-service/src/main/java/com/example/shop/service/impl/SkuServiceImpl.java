package com.example.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.Sku;
import com.example.shop.service.SkuService;
import com.example.shop.mapper.SkuMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【sku(单品SKU表)】的数据库操作Service实现
* @createDate 2023-05-28 17:53:04
*/
@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku>
    implements SkuService{

}




