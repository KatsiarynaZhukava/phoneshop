package com.es.core.util;

import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StockRowMapper implements RowMapper<Stock> {

    @Override
    public Stock mapRow( final ResultSet resultSet, int rowNumber ) throws SQLException {
        Phone phone = new Phone();
        phone.setId(resultSet.getLong("phoneId"));
        return new Stock(phone, resultSet.getLong("stock"), resultSet.getLong("reserved"));
    }
}