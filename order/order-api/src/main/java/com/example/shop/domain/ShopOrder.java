package com.example.shop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@ApiModel("店铺订单对象")
public class ShopOrder {

    //@ApiModelProperty("商品条目集合")
    private List<OrderItem> shopCartItemDiscounts;

    //@ApiModelProperty("店铺满减")
    private BigDecimal shopReduce = BigDecimal.ZERO;

    //@ApiModelProperty("店铺运费")
    private BigDecimal transfee = BigDecimal.ZERO;


}