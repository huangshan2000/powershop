package com.example.shop.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shop.domain.Prod;
import com.example.shop.domain.ProdTagReference;
import com.example.shop.domain.Sku;
import com.example.shop.service.ProdService;
import com.example.shop.mapper.ProdMapper;
import com.example.shop.service.ProdTagReferenceService;
import com.example.shop.service.SkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lenovo
 * @description 针对表【prod(商品)】的数据库操作Service实现
 * @createDate 2023-05-28 17:53:04
 */
@Service
@RequiredArgsConstructor
public class ProdServiceImpl extends ServiceImpl<ProdMapper, Prod>
        implements ProdService {

    private final ProdMapper prodMapper;

    private final ProdTagReferenceService prodTagReferenceService;

    private final SkuService skuService;

    @Transactional
    @Override
    public boolean save(Prod prod) {
        //新增商品
        int prodFlag = prodMapper.insert(
                prod.setShopId(1L)
                        .setSoldNum(0)
                        .setDeliveryMode(
                                JSON.toJSONString(prod.getDeliveryModeVo())
                        )
                        .setOriPrice(prod.getPrice())
                        .setCreateTime(new Date())
                        .setUpdateTime(new Date())
                        .setPutawayTime(new Date())
                        .setVersion(1)
        );

        if (prodFlag <= 0)
            throw new RuntimeException("新增商品失败");

        //新增分组和商品关联关系
        List<ProdTagReference> referenceList = prod.getTagList()
                .stream()
                .map(
                        tagId -> ProdTagReference
                                .builder()
                                .tagId(tagId)
                                .shopId(1L)
                                .status(1)
                                .prodId(prod.getProdId())
                                .build()

                ).collect(Collectors.toList());

        //批量新增操作
        boolean referenceFlag = prodTagReferenceService.saveBatch(referenceList);

        if (!referenceFlag)
            throw new RuntimeException("新增商品分组关联关系失败");

        //新增sku集合
        List<Sku> skuList = prod.getSkuList();

        skuList.forEach(
                sku -> {
                    sku.setProdId(prod.getProdId())
                            .setActualStocks(sku.getStocks())
                            .setUpdateTime(new Date())
                            .setRecTime(new Date())
                            .setVersion(1)
                            .setIsDelete(0);
                }
        );

        //批量新增skuList集合
        boolean skuFlag = skuService.saveBatch(skuList);

        if(!skuFlag)
            throw new RuntimeException("新增商品库存信息失败");

        return true;
        
    }
}




