package com.example.shop.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 主页轮播图
 * @TableName index_img
 */
@TableName(value ="index_img")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class IndexImg implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "img_id", type = IdType.AUTO)
    private Long imgId;

    /**
     * 店铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 图片
     */
    @TableField(value = "img_url")
    private String imgUrl;

    /**
     * 说明文字,描述
     */
    @TableField(value = "des")
    private String des;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 链接
     */
    @TableField(value = "link")
    private String link;

    /**
     * 状态
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 顺序
     */
    @TableField(value = "seq")
    private Integer seq;

    /**
     * 上传时间
     */
    @TableField(value = "upload_time")
    private Date uploadTime;

    /**
     * 关联
     */
    @TableField(value = "relation")
    private Long relation;

    /**
     * 类型
     */
    @TableField(value = "type")
    private Integer type;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}