package com.calt.burox.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.calt.burox.domain.RolePermission;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link RolePermission} entity.
 */
public interface RolePermissionSearchRepository
    extends ReactiveElasticsearchRepository<RolePermission, Long>, RolePermissionSearchRepositoryInternal {}

interface RolePermissionSearchRepositoryInternal {
    Flux<RolePermission> search(String query);

    Flux<RolePermission> search(Query query);
}

class RolePermissionSearchRepositoryInternalImpl implements RolePermissionSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    RolePermissionSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<RolePermission> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Flux<RolePermission> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, RolePermission.class).map(SearchHit::getContent);
    }
}
