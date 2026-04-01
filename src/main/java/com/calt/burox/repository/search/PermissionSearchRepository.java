package com.calt.burox.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.calt.burox.domain.Permission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Permission} entity.
 */
public interface PermissionSearchRepository extends ReactiveElasticsearchRepository<Permission, Long>, PermissionSearchRepositoryInternal {}

interface PermissionSearchRepositoryInternal {
    Flux<Permission> search(String query, Pageable pageable);

    Flux<Permission> search(Query query);
}

class PermissionSearchRepositoryInternalImpl implements PermissionSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    PermissionSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Permission> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<Permission> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Permission.class).map(SearchHit::getContent);
    }
}
