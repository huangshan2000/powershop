package com.example.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;

/**
 * @author: William
 * @date: 2023-05-28 22:40
 **/
@Data
@NoArgsConstructor
public class R<T> extends HttpEntity<T> {
    public R(T body) {
        super(body);
    }
}
