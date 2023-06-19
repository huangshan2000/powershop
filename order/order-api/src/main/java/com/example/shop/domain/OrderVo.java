package com.example.shop.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
//@ApiModel("订单确认的返回对象")
public class OrderVo {

    //@ApiModelProperty("用户的收货地址")
    private UserAddr userAddr;

    //@ApiModelProperty("店铺集合")
    private List<ShopOrder> shopCartOrders;

    //@ApiModelProperty("订单商品总数量")
    private Integer totalCount;

    //@ApiModelProperty("实际金额")
    private BigDecimal actualTotal;

    //@ApiModelProperty("总金额")
    private BigDecimal total;

    //@ApiModelProperty("满减")
    private BigDecimal shopReduce;

    //@ApiModelProperty("运费")
    private BigDecimal transfee;

    //@ApiModelProperty("买家留言")
    private String remarks;

    //@ApiModelProperty("选择的优惠券id")
    private List<Long> couponIds;

}
