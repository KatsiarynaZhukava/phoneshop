package com.es.core.model.phone;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ColorRowMapper implements RowMapper<Color> {
    private static final Map<Long, Color> existingColors = new ConcurrentHashMap<>();

    @Override
    public Color mapRow( final ResultSet resultSet, int rowNumber ) throws SQLException {
        Long id = resultSet.getLong("colors.id");
        String code = resultSet.getString("code");
        return existingColors.computeIfAbsent(id, key -> new Color(key, code));
    }
}