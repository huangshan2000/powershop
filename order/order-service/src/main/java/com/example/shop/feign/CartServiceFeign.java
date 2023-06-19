package com.example.shop.feign;

import com.example.shop.domain.Basket;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: William
 * @date: 2023-06-16 01:06
 **/
@FeignClient("cart-service")
public interface CartServiceFeign {
    //根据购物车ids数据，查询集合数据
    @GetMapping("/p/shopCart/list")
     List<Basket> list(@RequestParam("ids") List<Long> ids);

    @DeleteMapping("/p/shopCart/clearCart")
    boolean clearCart(@RequestParam("userId") String userId, @RequestParam("skuIds") List<Long> skuIds);
}
