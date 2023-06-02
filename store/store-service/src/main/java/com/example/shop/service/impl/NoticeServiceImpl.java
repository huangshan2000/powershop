package com.example.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.Notice;
import com.example.shop.service.NoticeService;
import com.example.shop.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【notice】的数据库操作Service实现
* @createDate 2023-06-02 10:18:40
*/
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>
    implements NoticeService{

}




