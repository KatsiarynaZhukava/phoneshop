package com.es.core.model.phone;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ColorRowMapper implements RowMapper<Color> {
    private static final Set<Color> existingColors = Collections.synchronizedSet(new HashSet<>());

    @Override
    public Color mapRow( final ResultSet resultSet, int rowNumber ) throws SQLException {
        long colorId = resultSet.getLong("colorId");
        String code = resultSet.getString("code");
        Optional<Color> maybeColor = existingColors.stream()
                                                   .filter(color -> color.getId() == colorId)
                                                   .filter(color -> color.getCode() == code)
                                                   .findFirst();
        if (maybeColor.isPresent()) {
            return maybeColor.get();
        } else {
            Color color = new Color();
            color.setId(colorId);
            color.setCode(resultSet.getString("code"));
            existingColors.add(color);
            return color;
        }
    }
}