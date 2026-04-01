package com.calt.burox.repository.rowmapper;

import com.calt.burox.domain.RolePermission;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RolePermission}, with proper type conversions.
 */
@Service
public class RolePermissionRowMapper implements BiFunction<Row, String, RolePermission> {

    private final ColumnConverter converter;

    public RolePermissionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RolePermission} stored in the database.
     */
    @Override
    public RolePermission apply(Row row, String prefix) {
        RolePermission entity = new RolePermission();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setRoleId(converter.fromRow(row, prefix + "_role_id", Long.class));
        entity.setPermissionId(converter.fromRow(row, prefix + "_permission_id", Long.class));
        return entity;
    }
}
