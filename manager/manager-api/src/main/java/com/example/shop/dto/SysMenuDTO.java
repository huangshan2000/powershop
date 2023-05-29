package com.example.shop.dto;

import com.example.shop.domain.SysMenu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: William
 * @date: 2023-05-28 22:45
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysMenuDTO {
    private String menuId;
    private String name;
    private String icon;
    private String url;

    private List<SysMenu> list;
}
