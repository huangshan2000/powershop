package com.example.shop.base;

import com.example.shop.entity.R;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author: William
 * @date: 2023-05-30 10:12
 **/
public class Base {
    //获取用户的id，前台用户是String类型的用户id，后台用户是Long类型用户id
    protected String getWxUserId() {
        return getAuthentication().getName();
    }

    protected Long getUserId() {
        return Long.valueOf(getAuthentication().getName());
    }


    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    protected <T> R<T> ok(T data) {
        return new  R<T>(data);
    }

    protected <T> R<T> ok() {
        return this.ok(null);
    }
}
