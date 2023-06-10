package com.example.shop.feign;

import com.example.shop.domain.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: William
 * @date: 2023-06-10 15:38
 **/
@FeignClient("product-service")
public interface ProductServiceFeign {

    @GetMapping("/prod/prod/sku/list")
    List<Sku> skuList(@RequestParam("ids") List<Long> ids);
}
