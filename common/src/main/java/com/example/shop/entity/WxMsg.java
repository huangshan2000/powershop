package com.example.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: William
 * @date: 2023-06-09 11:36
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class WxMsg {

    private String touser;//发送的微信公众号
    private String template_id; //使用的模板
    private String url; //点击微信公众号，跳转的路径地址
    private String topcolor; //有多条消息时，顶部会出现一个多个的消息的层级的颜色
    private Map<String,Map<String,String>> data;

    public void appenData(String key, String color, String value) {
        if (ObjectUtils.isEmpty(data))
            data = new HashMap<>();

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("value", value);
        dataMap.put("color", color);
        data.put(key, dataMap);
    }

    //class WxData{
    //    private String key;
    //    private WxDataMap wxDataMap;
    //
    //    class WxDataMap {
    //        private String value;
    //        private String color;
    //    }
    //}

}
