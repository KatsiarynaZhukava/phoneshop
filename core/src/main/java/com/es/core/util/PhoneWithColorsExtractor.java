package com.es.core.util;

import com.es.core.model.phone.Color;
import com.es.core.model.phone.Phone;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class PhoneWithColorsExtractor implements ResultSetExtractor<List<Phone>> {
    @Resource
    private ColorRowMapper colorRowMapper;
    @Resource
    private BeanPropertyRowMapper<Phone> phoneBeanPropertyRowMapper;

    @Override
    public List<Phone> extractData( final ResultSet resultSet ) throws SQLException, DataAccessException {
        Map<Long, Phone> phonesMap = new HashMap<>();
        List<Phone> phonesList = new ArrayList<>();

        while (resultSet.next()) {
            Long id = resultSet.getLong("id");
            Phone phone = phonesMap.get(id);
            if (phone == null) {
                phone = phoneBeanPropertyRowMapper.mapRow(resultSet, resultSet.getRow());
                phone.setId(id);
                phonesMap.put(id, phone);
                phonesList.add(phone);
            }
            if (resultSet.getLong("colorId") != 0 && resultSet.getString("code") != null) {
                Color color = colorRowMapper.mapRow(resultSet, resultSet.getRow());
                Objects.requireNonNull(phone).getColors().add(color);
            }
        }
        return phonesList;
    }
}