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
 * 商品收藏表
 * @TableName prod_favorite
 */
@TableName(value ="prod_favorite")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ProdFavorite implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "favorite_id", type = IdType.AUTO)
    private Long favoriteId;

    /**
     * 商品ID
     */
    @TableField(value = "prod_id")
    private Long prodId;

    /**
     * 收藏时间
     */
    @TableField(value = "rec_time")
    private Date recTime;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private String userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}