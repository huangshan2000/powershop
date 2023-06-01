package com.example.shop.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品
 *
 * @TableName prod
 */
@TableName(value = "prod")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class Prod implements Serializable {
    /**
     * 产品ID
     */
    @TableId(value = "prod_id", type = IdType.AUTO)
    private Long prodId;

    /**
     * 商品名称
     */
    @TableField(value = "prod_name")
    private String prodName;

    /**
     * 店铺id
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 原价
     */
    @TableField(value = "ori_price")
    private BigDecimal oriPrice;

    /**
     * 现价
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 简要描述,卖点等
     */
    @TableField(value = "brief")
    private String brief;

    /**
     * 详细描述
     */
    @TableField(value = "content")
    private String content;

    /**
     * 商品主图
     */
    @TableField(value = "pic")
    private String pic;

    /**
     * 商品图片，以,分割
     */
    @TableField(value = "imgs")
    private String imgs;

    /**
     * 默认是1，表示正常状态, -1表示删除, 0下架
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 商品分类
     */
    @TableField(value = "category_id")
    private Long categoryId;

    /**
     * 销量
     */
    @TableField(value = "sold_num")
    private Integer soldNum;

    /**
     * 总库存
     */
    @TableField(value = "total_stocks")
    private Integer totalStocks;

    /**
     * 配送方式json见TransportModeVO
     */
    @TableField(value = "delivery_mode")
    private Object deliveryMode;

    /**
     * 运费模板id
     */
    @TableField(value = "delivery_template_id")
    private Long deliveryTemplateId;

    /**
     * 录入时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 上架时间
     */
    @TableField(value = "putaway_time")
    private Date putawayTime;

    /**
     * 版本 乐观锁
     */
    @TableField(value = "version")
    @Version
    private Integer version;

    @TableField(exist = false)
    private List<Sku> skuList;

    @TableField(exist = false)
    private List<Long> tagList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private deliveryModeVo deliveryModeVo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class deliveryModeVo {
        private Boolean hasUserPickUp;
        private Boolean hasShopDelivery;
    }
}