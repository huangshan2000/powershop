package com.example.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shop.base.BaseStore;
import com.example.shop.domain.Area;
import com.example.shop.entity.R;
import com.example.shop.service.AreaService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: William
 * @date: 2023-06-02 10:34
 **/
@RestController
@RequestMapping("/admin/area")
@RequiredArgsConstructor

public class AreaController extends BaseStore {

    private final AreaService areaService;

    @GetMapping("/list")
    public R<List<Area>> list() {
        return ok(
                areaService.list()
        );
    }

    @PostMapping
    public R<Boolean> saveArea(@RequestBody Area area) {
        //获取parentId，判断层级级别
        Long parentId = area.getParentId();

        if (ObjectUtils.isEmpty(parentId))
            area.setLevel(1);
        else {
            //设置市或区的层级级别
            //查询父层级
            Integer parentLevel = areaService.getById(parentId).getLevel();

            if (parentLevel.intValue() == 3)
                area.setLevel(3);
            else
                area.setLevel(
                        parentLevel.intValue() + 1
                );
        }

        return ok(
                areaService.save(area)
        );
    }

    @GetMapping("/listByPid")
    public R<List<Area>> listById(@RequestParam("pid") Long  pid) {
        return ok(
                areaService.list(
                        new LambdaQueryWrapper<Area>()
                                .eq(ObjectUtils.isNotEmpty(pid),Area::getParentId,pid)
                )
        );
    }
}
