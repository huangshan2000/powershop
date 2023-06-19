package com.example.shop.feign;

import com.example.shop.domain.User;
import com.example.shop.domain.UserAddr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: William
 * @date: 2023-06-16 11:26
 **/
@FeignClient("member-service")
public interface MemberServiceFeign {

    @GetMapping("/p/address/default")
     UserAddr defaultUserAddr(@RequestParam("userId") String userId);

    @GetMapping("/p/user/{id}")
    User getUser(@PathVariable("id") String id);
}
