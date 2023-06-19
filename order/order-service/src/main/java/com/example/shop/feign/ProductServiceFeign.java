package com.example.shop.feign;

import com.example.shop.domain.ProdEsCount;
import com.example.shop.domain.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: William
 * @date: 2023-06-15 19:00
 **/
@FeignClient("product-service")
public interface ProductServiceFeign {

    @GetMapping("/prod/prod/sku/list")
    List<Sku> skuList(@RequestParam("ids") List<Long> ids);

    @PutMapping("/prod/prod/deductMySqlStock")
    boolean deductMySqlStock(List<ProdEsCount> countList);
}
