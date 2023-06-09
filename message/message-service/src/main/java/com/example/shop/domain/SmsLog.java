package com.example.shop.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * 短信记录表
 * @TableName sms_log
 */
@TableName(value ="sms_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SmsLog implements Serializable {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 手机号码
     */
    @TableField(value = "user_phone")
    private String userPhone;

    /**
     * 短信内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 手机验证码
     */
    @TableField(value = "mobile_code")
    private String mobileCode;

    /**
     * 短信类型  1:注册  2:验证
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 发送时间
     */
    @TableField(value = "rec_date")
    private Date recDate;

    /**
     * 发送短信返回码
     */
    @TableField(value = "response_code")
    private String responseCode;

    /**
     * 状态  1:有效  0：失效
     */
    @TableField(value = "status")
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}