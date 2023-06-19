package com.example.shop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@ApiModel("订单确认的入参对象")
public class OrderConfirm {

    /*
        在购物车列表页面点击结算按钮
     */
    //@ApiModelProperty("购物车的ids")
    private List<Long> basketIds;

    /*
        在商品详情页面点击立即购买按钮
     */
    //@ApiModelProperty("订单条目对象")
    private OrderItem orderItem;

}