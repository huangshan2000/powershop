package com.example.shop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@ApiModel("购物车中的金额对象")
public class CartMoney {


    //@ApiModelProperty("最终金额")
    private BigDecimal finalMoney = BigDecimal.ZERO;

    //@ApiModelProperty("总金额")
    private BigDecimal totalMoney = BigDecimal.ZERO;

    //@ApiModelProperty("优惠金额")
    private BigDecimal subtractMoney = BigDecimal.ZERO;

    //@ApiModelProperty("运费金额")
    private BigDecimal transMoney = BigDecimal.ZERO;

}