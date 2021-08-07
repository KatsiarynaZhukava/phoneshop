package com.es.core.model.phone;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class ColorRowMapper implements RowMapper<Color> {
    private static final Map<Long, Color> existingColors = Collections.synchronizedMap(new HashMap<>());

    @Override
    public Color mapRow( final ResultSet resultSet, int rowNumber ) throws SQLException {
        long colorId = resultSet.getLong("colorId");
        Color color = existingColors.getOrDefault(colorId, new Color(colorId, resultSet.getString("code")));
        existingColors.putIfAbsent(colorId, color);
        return color;
    }
}