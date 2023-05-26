package com.example.shop.contants;

/**
 * @author: William
 * @date: 2023-05-25 16:20
 **/
public interface AuthServerConstants {

    /**
     * 请求头，登录的状态
     */
    String LOGIN_TYPE = "loginType";

    /**
     * 登录标记，后台系统用户
     */
    String SYS_USER = "sysUser";

    /**
     * 登录标记，前台系统用户
     */
    String FRONT_USER = "user";

    /**
     * 字符串的分隔符
     */
    String SEPARATOR = ",";

    /**
     * 私钥地址路径
     */
    String  PRIVATE_KEY_PATH = "rsa/sz2212.jks";

    /**
     * 认证密码
     */
    String   PASSWORD = "sz2212";
}
