package com.example.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.contants.AuthServerConstants;
import com.example.shop.domain.SysUser;
import com.example.shop.service.SysUserService;
import com.example.shop.mapper.SysUserMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Lenovo
* @description 针对表【sys_user(系统用户)】的数据库操作Service实现
* @createDate 2023-05-25 15:44:49
*/


@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService, UserDetailsService {

    @Autowired
    SysUserMapper sysUserMapper;

    /**
     * 数据库认证方法
     *  请求头loginType
     *      sysUser
     *          后台系统用户，根据用户名查询数据库的用户信息，再查询用户的菜单信息（权限）
     *      user
     *          其那台系统用户（微信小程序），微信第三方登录(oauth2验证码授权)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //获取请求对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        //获取请求头信息，用户的登录状态
        String loginType = request.getHeader(AuthServerConstants.LOGIN_TYPE);

        switch (loginType) {
            //后台系统用户的登录操作
            case AuthServerConstants.SYS_USER:
                //根据用户名查询用户信息
                SysUser sysUser = sysUserMapper.selectOne(
                        new LambdaQueryWrapper<SysUser>()
                                .eq(SysUser::getStatus, 1)
                                .eq(StringUtils.isNotBlank(username), SysUser::getUsername, username)
                );

                //用户不为空，查询用户的权限信息，校验交给oauth2框架来做
                if (ObjectUtils.isNotEmpty(sysUser)) {
                    //查询权限信息
                    List<String> auths = sysUserMapper.selectPermsList(sysUser.getUserId());

                    return sysUser.setAuths(auths);
                }

                break;

            case AuthServerConstants.FRONT_USER:

                break;
        }

        //代表用户登录失败，或用户名或密码错误
        return null;
    }
}




