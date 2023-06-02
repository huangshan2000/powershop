package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shop.base.BaseMember;
import com.example.shop.domain.Prod;
import com.example.shop.domain.UserCollection;
import com.example.shop.entity.R;
import com.example.shop.feign.ProductServiceFeign;
import com.example.shop.service.UserCollectionService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: William
 * @date: 2023-06-02 22:19
 **/
@RestController
@RequestMapping("p/collection")
@RequiredArgsConstructor
public class UserCollectionController extends BaseMember {

    private final UserCollectionService userCollectionService;

    private final ProductServiceFeign productServiceFeign;

    @GetMapping("/count")
    public R<Integer> count() {
        return ok(
                userCollectionService.count(
                        new LambdaQueryWrapper<UserCollection>()
                                .eq(UserCollection::getUserId, getWxUserId())
                )
        );
    }

    @GetMapping("/prods")
    public R<Page<Prod>> prods(Page<UserCollection> page) {
        //分页查询出当前收藏的商品数据
        Page<UserCollection> collectionPage = userCollectionService.page(
                page,
                new LambdaQueryWrapper<UserCollection>()
                        .eq(UserCollection::getUserId, getWxUserId())
        );

        List<UserCollection> records = collectionPage.getRecords();

        if (CollectionUtils.isEmpty(records))
            return ok(new Page<>(page.getCurrent(),page.getPages()));

        //收集商品id
        List<Long> prodIds = records.stream().map(UserCollection::getProdId).collect(Collectors.toList());

        //远程调用查询商品列表数据
        List<Prod> prodList = productServiceFeign.list(prodIds);

        //封装到Page对象中
        Page<Prod> prodPage = new Page<>(collectionPage.getCurrent(), collectionPage.getSize(), collectionPage.getTotal());
        prodPage.setRecords(prodList);

        //返回
        return ok(prodPage);
    }
}
