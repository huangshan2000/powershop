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

/**
 * 
 * @TableName prod_prop
 */
@TableName(value ="prod_prop")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ProdProp implements Serializable {
    /**
     * 属性id
     */
    @TableId(value = "prop_id", type = IdType.AUTO)
    private Long propId;

    /**
     * 属性名称
     */
    @TableField(value = "prop_name")
    private String propName;

    /**
     * ProdPropRule 1:销售属性(规格); 2:参数属性;
     */
    @TableField(value = "rule")
    private Integer rule;

    /**
     * 店铺id
     */
    @TableField(value = "shop_id")
    private Long shopId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}