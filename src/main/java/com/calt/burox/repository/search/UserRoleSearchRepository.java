package com.calt.burox.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.calt.burox.domain.UserRole;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link UserRole} entity.
 */
public interface UserRoleSearchRepository extends ReactiveElasticsearchRepository<UserRole, Long>, UserRoleSearchRepositoryInternal {}

interface UserRoleSearchRepositoryInternal {
    Flux<UserRole> search(String query);

    Flux<UserRole> search(Query query);
}

class UserRoleSearchRepositoryInternalImpl implements UserRoleSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    UserRoleSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<UserRole> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Flux<UserRole> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, UserRole.class).map(SearchHit::getContent);
    }
}
