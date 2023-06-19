package com.example.shop.controller;

import com.example.shop.base.BaseMember;
import com.example.shop.domain.User;
import com.example.shop.entity.R;
import com.example.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author: William
 * @date: 2023-06-02 17:19
 **/
@RestController
@RequestMapping("/p/user")
@RequiredArgsConstructor
public class UserController extends BaseMember {

    private final UserService userService;

    @PutMapping("/setUserInfo")
    public R<Boolean> setUserInfo(@RequestBody User user, HttpServletRequest request) {
        return ok(
                userService.updateById(
                        user.setUserId(getWxUserId())
                                .setSex(
                                        user.getSex().equals("0") ? "M" : "F"
                                )
                                .setModifyTime(new Date())
                                .setUserLasttime(new Date())
                                .setUserLastip(request.getRemoteAddr())
                )
        );
    }

    //-----------远程调用--------
    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") String id){
        return userService.getById(id);
    }
}
