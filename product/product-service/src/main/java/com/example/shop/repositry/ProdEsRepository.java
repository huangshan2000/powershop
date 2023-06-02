package com.example.shop.repositry;

import com.example.shop.domain.ProdEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdEsRepository extends ElasticsearchRepository<ProdEs,Long> {
}
