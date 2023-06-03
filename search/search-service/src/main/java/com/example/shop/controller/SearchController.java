package com.example.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shop.base.BaseSearch;
import com.example.shop.domain.ProdEs;
import com.example.shop.entity.R;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * @author: William
 * @date: 2023-06-03 11:47
 **/
@RestController
@RequiredArgsConstructor
public class SearchController extends BaseSearch {

    private final ElasticsearchRestTemplate restTemplate;

    /**
     * 商品查询
     * @param page
     * @param prodName
     * @param sort
     * @return
     */
    @GetMapping("/search/searchProdPage")
    public R<Page<ProdEs>> searchProdPage(Page page, @RequestParam("prodName")String prodName,@RequestParam("sort")Integer sort) {

        //创建条件对象，并封装关键字查询，分页查询，高亮查询，排序信息
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();

        //关键字查询，按照商品名称进行查询数据
        query.withQuery(
                QueryBuilders.matchQuery("prodName",prodName)
        );

        //分页查询
        query.withPageable(
                PageRequest.of(Long.valueOf(page.getCurrent()).intValue(),Long.valueOf(page.getSize()).intValue())
        );

        //排序信息
        String feildSort = "";
        SortOrder sortOrder = SortOrder.DESC;
        //0代表综合，按照好评率倒叙排序
        //1代表销量，按照销量倒叙排序
        //2代表价格，按照价格升序排序
        switch (sort) {
            case 0:
                feildSort = "positiveRating";
                sortOrder = SortOrder.DESC;
                break;
            case 1:
                feildSort = "soldNum";
                sortOrder = SortOrder.DESC;
                break;
            case 2:
                feildSort = "price";
                sortOrder = SortOrder.ASC;
                break;
        }
        query.withSort(
                SortBuilders.fieldSort(feildSort).order(sortOrder)
        );

        //高亮查询
        query.withHighlightFields(
                new HighlightBuilder.Field("prodName")
                        .preTags("<i style='color: red'>")
                        .postTags("</i>")
        );

        //执行查询操作
        SearchHits<ProdEs> search = restTemplate.search(query.build(), ProdEs.class);

        //封装结果集及高亮信息
        List<ProdEs> prodEsList = new ArrayList<>();

        search.getSearchHits().forEach(
                hit -> {
                    ProdEs content = hit.getContent();

                    //封装高亮结果集
                    String highLightResult = hit.getHighlightField("prodName").get(0);

                    if (StringUtils.isNotBlank(highLightResult))
                        content.setProdName(highLightResult);

                    prodEsList.add(
                            content
                    );
                }
        );

        Page<ProdEs> prodEsPage = new Page<>(page.getCurrent(), page.getSize(), search.getTotalHits());
        prodEsPage.setRecords(prodEsList);

        return ok(prodEsPage);


    }
}
