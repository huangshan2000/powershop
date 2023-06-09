package com.example.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.SmsLog;
import com.example.shop.service.SmsLogService;
import com.example.shop.mapper.SmsLogMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【sms_log(短信记录表)】的数据库操作Service实现
* @createDate 2023-06-06 01:08:40
*/
@Service
public class SmsLogServiceImpl extends ServiceImpl<SmsLogMapper, SmsLog>
    implements SmsLogService{

}




