package com.example.shop.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.contants.AuthServerConstants;
import com.example.shop.domain.SysUser;
import com.example.shop.domain.User;
import com.example.shop.mapper.UserMapper;
import com.example.shop.properties.WxProperties;
import com.example.shop.service.SysUserService;
import com.example.shop.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
* @author Lenovo
* @description 针对表【sys_user(系统用户)】的数据库操作Service实现
* @createDate 2023-05-25 15:44:49
*/


@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService, UserDetailsService {

    private final SysUserMapper sysUserMapper;

    private final UserMapper userMapper;

    private final WxProperties wxProperties;

    private final RestTemplate restTemplate;

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

                //第三方登录操作
                //获取appId和appSecret
                String appId = wxProperties.getAppId();
                String appSecret = wxProperties.getAppSecret();
                //封装请求地址中的参数信息
                String url = String.format(
                        wxProperties.getLoginUrl(),
                        appId,
                        appSecret,
                        username
                );

                //注入ResTemplate，发送请求
                String jsonResult = restTemplate.getForObject(url, String.class);

                if (StringUtils.isNoneBlank(jsonResult)) {
                    //解析json数据
                    JSONObject jsonObject = JSON.parseObject(jsonResult);

                    //判断是否登录成功，openId是否返回
                    if (jsonObject.containsKey("openid")) {
                        //登录成功，根据openid(userId)查询当前用户是否存在
                        String openid = jsonObject.getString("openid");

                        User user = userMapper.selectById(openid);

                        //获取ip地址
                        String ip = request.getRemoteAddr();

                        if (ObjectUtils.isEmpty(user)) {
                            //如果用户不存在，我们需要新增用户信息(自动注册)
                            userMapper.insert(
                                    user = User.builder()
                                            .userId(openid)
                                            .loginPassword(
                                                    new BCryptPasswordEncoder().encode("WECHAT")
                                            )
                                            .status(1)
                                            .modifyTime(new Date())
                                            .userRegtime(new Date())
                                            .userLasttime(new Date())
                                            .userRegip(ip)
                                            .userLastip(ip)
                                            .build()
                            );
                        }
                        //如果用户存在，则直接返回用户信息即可
                        return user;
                    }
                }
                break;
        }

        //代表用户登录失败，或用户名或密码错误
        return null;
    }
}




