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
 * 热搜
 * @TableName hot_search
 */
@TableName(value ="hot_search")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class HotSearch implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "hot_search_id", type = IdType.AUTO)
    private Long hotSearchId;

    /**
     * 店铺ID 0为全局热搜
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 录入时间
     */
    @TableField(value = "rec_date")
    private Date recDate;

    /**
     * 顺序
     */
    @TableField(value = "seq")
    private Integer seq;

    /**
     * 状态 0下线 1上线
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 热搜标题
     */
    @TableField(value = "title")
    private String title;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}