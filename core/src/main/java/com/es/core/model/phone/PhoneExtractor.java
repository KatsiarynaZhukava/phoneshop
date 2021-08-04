package com.es.core.model.phone;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhoneExtractor implements ResultSetExtractor<List<Phone>> {

    @Override
    public List<Phone> extractData( final ResultSet resultSet ) throws SQLException, DataAccessException {
        List<Phone> phones = new ArrayList();
        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            Phone phone = phones.stream()
                                .filter(phone1 -> id == phone1.getId())
                                .findFirst()
                                .orElse(null);
            if (phone == null) {
                phone = new BeanPropertyRowMapper<>(Phone.class).mapRow(resultSet, resultSet.getRow());
                phone.setId(id);
                phones.add(phone);
            }

            if (id != 0 && resultSet.getString("code") != null) {
                Color color = new ColorRowMapper().mapRow(resultSet, resultSet.getRow());
                phone.getColors().add(color);
            }
        }
        return phones;
    }
}