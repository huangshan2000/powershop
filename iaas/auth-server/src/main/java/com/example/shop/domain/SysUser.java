package com.example.shop.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.example.shop.contants.AuthServerConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 系统用户
 * @TableName sys_user
 */
@TableName(value ="sys_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysUser implements Serializable, UserDetails {
    /**
     * 
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 手机号
     */
    @TableField(value = "mobile")
    private String mobile;

    /**
     * 状态  0：禁用   1：正常
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建者ID
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 用户所在的商城Id
     */
    @TableField(value = "shop_id")
    private Long shopId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private List<String> auths;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //将auths权限信息，转换为框架所需的List<SimpleGrantedAuthority>
        //类型转换，这种方式不能够进行祝你换，因为业务中包含字符串拆分，得到的是一个集合，所以无法进行操作
//        auths.stream().map(
//                auth -> {
//                    //auth可能包含一个或多个的权限信息
//                    if (auth.contains(AuthServerConstants.SEPARATOR)) {
//                        //有多个权限信息，字符串拆分
//                        Arrays.asList(
//                                auth.split(AuthServerConstants.SEPARATOR)
//                        ).forEach(
//                                a ->
//                        );
//                    }
//                    //只有一个权限信息
//                    return  auth;
//                }
//        );


        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        auths.forEach(
                auth -> {
                    //auth可能包含一个或多个权限信息
                    if (auth.contains(AuthServerConstants.SEPARATOR)) {
                        //有多个权限信息，字符串拆分
                        Arrays.asList(
                                auth.split(AuthServerConstants.SEPARATOR)
                        ).forEach(
                                //遍历后对权限信息进行封装
                                a -> authorities.add(new SimpleGrantedAuthority(a))
                        );
                    }else
                        //只有一个权限信息
                    authorities.add(new SimpleGrantedAuthority(auth));
                }
        );
        return authorities;
    }

    /**
     * 可以通过Security查询到用户的名称，这里最好设置为用户的id
     * @return
     */
    public String getUsername() {
        return String.valueOf(this.userId);
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isEnabled() {
        return this.status == 1;
    }
}