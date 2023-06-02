package com.example.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.UserCollection;
import com.example.shop.service.UserCollectionService;
import com.example.shop.mapper.UserCollectionMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【user_collection】的数据库操作Service实现
* @createDate 2023-06-02 17:10:15
*/
@Service
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection>
    implements UserCollectionService{

}




