package com.calt.burox.repository.rowmapper;

import com.calt.burox.domain.UserRole;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link UserRole}, with proper type conversions.
 */
@Service
public class UserRoleRowMapper implements BiFunction<Row, String, UserRole> {

    private final ColumnConverter converter;

    public UserRoleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link UserRole} stored in the database.
     */
    @Override
    public UserRole apply(Row row, String prefix) {
        UserRole entity = new UserRole();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setRoleId(converter.fromRow(row, prefix + "_role_id", Long.class));
        return entity;
    }
}
