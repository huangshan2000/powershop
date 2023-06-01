package com.example.shop.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shop.base.BaseProduct;
import com.example.shop.domain.ProdProp;
import com.example.shop.domain.ProdPropValue;
import com.example.shop.entity.R;
import com.example.shop.service.ProdPropService;
import com.example.shop.service.ProdPropValueService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: William
 * @date: 2023-05-31 17:37
 **/
@RestController
@RequestMapping("/prod/spec")
@RequiredArgsConstructor
public class ProdPropController extends BaseProduct {

    private final ProdPropService prodPropService;

    private final ProdPropValueService prodPropValueService;

    @GetMapping("/page")
    public R<Page<ProdProp>> page(Page<ProdProp> page, @RequestParam(value = "propName", required = false) String propName) {
        //分页查询出商品属性列表数据
        Page<ProdProp> propPage = prodPropService.page(
                page,
                new LambdaQueryWrapper<ProdProp>()
                        .like(StringUtils.isNotBlank(propName), ProdProp::getPropName, propName)
        );

        List<ProdProp> records = propPage.getRecords();

        if (CollectionUtils.isEmpty(records))
            return ok(propPage);
        //再根据商品的属性id，查询出所有的属性值的列表数据
        List<Long> propIds = records.stream().map(ProdProp::getPropId).collect(Collectors.toList());

        List<ProdPropValue> prodPropValues = prodPropValueService.list(
                new LambdaQueryWrapper<ProdPropValue>()
                        .in(ProdPropValue::getPropId, propIds)
        );

        //遍历属性集合
        records.forEach(
                prop -> {
                    //根据属性id将属性值的集合过滤出来
                    List<ProdPropValue> valueList = prodPropValues.stream().filter(
                            value -> value.getPropId().equals(prop.getPropId())
                    ).collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(valueList))
                        prop.setProdPropValues(valueList);
                }
        );
        return ok(propPage);
    }

    @PostMapping
    public R<Boolean> savePropAndValues(@RequestBody ProdProp prodProp) {

        //新增属性
        boolean propFlag = prodPropService.save(
                prodProp.setShopId(1L)
                        .setRule(
                                //规格属性
                                //销售属性
                                1
                        )
        );

        if (!propFlag)
            throw new RuntimeException("新增属性失败！");

        List<ProdPropValue> values = prodProp.getProdPropValues();
        if (!CollectionUtils.isEmpty(values))
            //再将属性id封装到values集合中
            values.forEach(
                    value -> value.setPropId(prodProp.getPropId())
            );

        //批量新增values
        boolean valueFlag = prodPropValueService.saveBatch(values);

        if (!valueFlag)
            throw new RuntimeException("新增属性失败");

        return ok(
                propFlag && valueFlag
        );
    }

    @GetMapping("/list")
    public R<List<ProdProp>> list() {
        return ok(
                prodPropService.list()
        );
    }

    @GetMapping("/listSpecValue/{prodId}")
    public R<List<ProdPropValue>> listSpecValue(@PathVariable("prodId") Long propId) {
        return ok(
                prodPropValueService.list(
                        new LambdaQueryWrapper<ProdPropValue>()
                                .eq(ObjectUtils.isNotEmpty(propId),ProdPropValue::getPropId,propId)
                )
        );
    }


}
