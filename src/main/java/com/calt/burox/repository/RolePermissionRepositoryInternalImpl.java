package com.calt.burox.repository;

import com.calt.burox.domain.RolePermission;
import com.calt.burox.repository.rowmapper.PermissionRowMapper;
import com.calt.burox.repository.rowmapper.RolePermissionRowMapper;
import com.calt.burox.repository.rowmapper.RoleRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the RolePermission entity.
 */
@SuppressWarnings("unused")
class RolePermissionRepositoryInternalImpl extends SimpleR2dbcRepository<RolePermission, Long> implements RolePermissionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RoleRowMapper roleMapper;
    private final PermissionRowMapper permissionMapper;
    private final RolePermissionRowMapper rolepermissionMapper;

    private static final Table entityTable = Table.aliased("role_permission", EntityManager.ENTITY_ALIAS);
    private static final Table roleTable = Table.aliased("role", "e_role");
    private static final Table permissionTable = Table.aliased("permission", "permission");

    public RolePermissionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RoleRowMapper roleMapper,
        PermissionRowMapper permissionMapper,
        RolePermissionRowMapper rolepermissionMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(RolePermission.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.rolepermissionMapper = rolepermissionMapper;
    }

    @Override
    public Flux<RolePermission> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<RolePermission> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RolePermissionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RoleSqlHelper.getColumns(roleTable, "role"));
        columns.addAll(PermissionSqlHelper.getColumns(permissionTable, "permission"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(roleTable)
            .on(Column.create("role_id", entityTable))
            .equals(Column.create("id", roleTable))
            .leftOuterJoin(permissionTable)
            .on(Column.create("permission_id", entityTable))
            .equals(Column.create("id", permissionTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, RolePermission.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<RolePermission> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<RolePermission> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private RolePermission process(Row row, RowMetadata metadata) {
        RolePermission entity = rolepermissionMapper.apply(row, "e");
        entity.setRole(roleMapper.apply(row, "role"));
        entity.setPermission(permissionMapper.apply(row, "permission"));
        return entity;
    }

    @Override
    public <S extends RolePermission> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
