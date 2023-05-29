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
 * @date: 2023-05-28 22:43
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MenuAndAuth {
    private List<SysMenu> menuList;
    //private List<SysMenuDTO> menuList;
    private List<String> authorities;
}
