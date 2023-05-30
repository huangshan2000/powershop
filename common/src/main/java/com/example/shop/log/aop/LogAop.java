package com.example.shop.log.aop;

import com.alibaba.fastjson.JSON;
import com.example.shop.log.anno.Log;
import com.example.shop.log.domain.SystemLog;
import com.example.shop.log.mapper.SystemLogMapper;
import com.example.shop.log.mapper.SystemUserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;

/**
 * @author: William
 * @date: 2023-05-30 14:58
 **/
@Aspect
@Configuration
@RequiredArgsConstructor
public class LogAop {

    private final SystemLogMapper systemLogMapper;

    private final SystemUserMapper systemUserMapper;

    /**
     * 环绕通知
     * 通过环绕通知来进行aop的增强(前置通知、后置通知)，来记录日志信息
     */
    @Around("@annotation(com.example.shop.log.anno.Log)")
    public Object logAround(ProceedingJoinPoint joinPoint) {

        try {
            //获取参数信息
            Object[] args = joinPoint.getArgs();

            //创建方法执行开始时间
            long start = System.currentTimeMillis();

            //执行目标方法
            Object proceed = joinPoint.proceed(args);

            //创建方法执行结束时间
            long end = System.currentTimeMillis();

            //获取ip地址
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            String remoteAddr = "";

            if (ObjectUtils.isEmpty(request))
                remoteAddr = "127.0.0.1";
            else
                remoteAddr = request.getRemoteAddr();

            //获取Method对象
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

            //获取的控制器的权限定路径 com.example.shop.controller.SysUserController
            String declaringTypeName = methodSignature.getDeclaringTypeName();

            System.out.println(String.format("declaringTypeName = {%s}",declaringTypeName));

            Method method = methodSignature.getMethod();

            //获取修饰符
            /*
                PUBLIC: 1 （二进制 0000 0001）
                PRIVATE: 2 （二进制 0000 0010）
                PROTECTED: 4 （二进制 0000 0100）
                STATIC: 8 （二进制 0000 1000）
                FINAL: 16 （二进制 0001 0000）
                SYNCHRONIZED: 32 （二进制 0010 0000）
                VOLATILE: 64 （二进制 0100 0000）
                TRANSIENT: 128 （二进制 1000 0000）
                NATIVE: 256 （二进制 0001 0000 0000）
                INTERFACE: 512 （二进制 0010 0000 0000）
                ABSTRACT: 1024 （二进制 0100 0000 0000）
                STRICT: 2048 （二进制 1000 0000 0000）
             */
            int modifiers = method.getModifiers();

            String modifiersName = "";

            String modifier = Modifier.toString(method.getModifiers());
            switch (modifiers) {
                case 1:
                    modifiersName = "PUBLIC";
                    break;
                case 2:
                    modifiersName = "PRIVATE";
                    break;
                case 4:
                    modifiersName = "PROTECTED";
                    break;

            }

            //获取方法名称
            //String declearingName = method.getDeclaringClass().getName();

            //System.out.println(String.format("declearingName = {%s}",declearingName));

            String methodName = method.getName();
            //System.out.println(String.format("methodName = {%s}",methodName));

            String returnTypeName = method.getReturnType().getName();

            //获取注解中的operation操作的属性信息
            Log logAnno = method.getAnnotation(Log.class);

            //获取注解中的属性信息
            String operation = logAnno.operation();

            //从security上下文对象获取的是userId，根据用户id查询用户名称
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();

            String username = "";

            if (StringUtils.isBlank(userId))
                username = "匿名用户";
            else
                username = systemUserMapper.selectById(userId).getUsername();

            //记录日志信息
            systemLogMapper.insert(
                    SystemLog.builder()
                            .username(username)
                            .operation(operation)
                            .method(
                                    modifiersName
                                            + returnTypeName
                                            + " " +
                                            declaringTypeName
                                            + "." +
                                            methodName
                            )
                            .params(
                                    JSON.toJSONString(args)
                            )
                            .time(
                                    end - start
                            )
                            .ip(remoteAddr)
                            .createDate(new Date())
                            .build()
            );

            return proceed;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
