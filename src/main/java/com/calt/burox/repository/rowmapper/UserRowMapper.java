package com.calt.burox.repository.rowmapper;

import com.calt.burox.domain.User;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link User}, with proper type conversions.
 */
@Service
public class UserRowMapper implements BiFunction<Row, String, User> {

    private final ColumnConverter converter;

    public UserRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link User} stored in the database.
     */
    @Override
    public User apply(Row row, String prefix) {
        User entity = new User();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUsername(converter.fromRow(row, prefix + "_username", String.class));
        entity.setPassword(converter.fromRow(row, prefix + "_password", String.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", Instant.class));
        return entity;
    }
}
