package com.calt.burox.repository;

import com.calt.burox.domain.UserRole;
import com.calt.burox.repository.rowmapper.RoleRowMapper;
import com.calt.burox.repository.rowmapper.UserRoleRowMapper;
import com.calt.burox.repository.rowmapper.UserRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the UserRole entity.
 */
@SuppressWarnings("unused")
class UserRoleRepositoryInternalImpl extends SimpleR2dbcRepository<UserRole, Long> implements UserRoleRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final RoleRowMapper roleMapper;
    private final UserRoleRowMapper userroleMapper;

    private static final Table entityTable = Table.aliased("user_role", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("jhi_user", "e_user");
    private static final Table roleTable = Table.aliased("role", "e_role");

    public UserRoleRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        RoleRowMapper roleMapper,
        UserRoleRowMapper userroleMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(UserRole.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userroleMapper = userroleMapper;
    }

    @Override
    public Flux<UserRole> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<UserRole> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = UserRoleSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(RoleSqlHelper.getColumns(roleTable, "role"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(roleTable)
            .on(Column.create("role_id", entityTable))
            .equals(Column.create("id", roleTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, UserRole.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<UserRole> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<UserRole> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private UserRole process(Row row, RowMetadata metadata) {
        UserRole entity = userroleMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setRole(roleMapper.apply(row, "role"));
        return entity;
    }

    @Override
    public <S extends UserRole> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
