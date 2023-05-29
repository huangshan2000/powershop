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
 * @TableName prod_prop_value
 */
@TableName(value ="prod_prop_value")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ProdPropValue implements Serializable {
    /**
     * 属性值ID
     */
    @TableId(value = "value_id", type = IdType.AUTO)
    private Long valueId;

    /**
     * 属性值名称
     */
    @TableField(value = "prop_value")
    private String propValue;

    /**
     * 属性ID
     */
    @TableField(value = "prop_id")
    private Long propId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}