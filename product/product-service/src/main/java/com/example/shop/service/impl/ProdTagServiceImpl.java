package com.example.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.ProdTag;
import com.example.shop.service.ProdTagService;
import com.example.shop.mapper.ProdTagMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【prod_tag(商品分组表)】的数据库操作Service实现
* @createDate 2023-05-28 17:53:04
*/
@Service
public class ProdTagServiceImpl extends ServiceImpl<ProdTagMapper, ProdTag>
    implements ProdTagService{

}




