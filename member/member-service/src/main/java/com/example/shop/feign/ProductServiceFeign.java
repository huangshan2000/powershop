package com.example.shop.feign;

import com.example.shop.domain.Prod;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: William
 * @date: 2023-06-02 23:26
 **/
@FeignClient("product-service")
public interface ProductServiceFeign {

    @GetMapping("/prod/prod/list")
    List<Prod> list(@RequestParam("ids")List<Long> ids);
}
