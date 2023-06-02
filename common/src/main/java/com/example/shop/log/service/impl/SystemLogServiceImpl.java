package com.example.shop.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.log.domain.SystemLog;
import com.example.shop.log.mapper.SystemLogMapper;
import com.example.shop.log.service.SystemLogService;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【sys_log(系统日志)】的数据库操作Service实现
* @createDate 2023-05-30 14:55:01
*/
@Service
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog>
    implements SystemLogService {

}




