package com.es.core.model.phone;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PhoneWithColorsExtractor implements ResultSetExtractor<List<Phone>> {
    @Resource
    private ColorRowMapper colorRowMapper;
    @Resource
    private BeanPropertyRowMapper<Phone> phoneBeanPropertyRowMapper;

    @Override
    public List<Phone> extractData( final ResultSet resultSet ) throws SQLException, DataAccessException {
        Map<Long, Phone> phones = new HashMap<>();
        while (resultSet.next()) {
            Long id = resultSet.getLong("id");
            Phone phone = phones.getOrDefault(id, phoneBeanPropertyRowMapper.mapRow(resultSet, resultSet.getRow()));
            phone.setId(id);
            phones.put(id, phone);

            if (resultSet.getLong("colorId") != 0 && resultSet.getString("code") != null) {
                Color color = colorRowMapper.mapRow(resultSet, resultSet.getRow());
                phone.getColors().add(color);
            }
        }
        return new ArrayList(phones.values());
    }
}