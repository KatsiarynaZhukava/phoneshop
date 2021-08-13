package com.es.core.dao;

import com.es.core.model.phone.Stock;
import com.es.core.util.StockRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Component
public class JdbcStockDao implements StockDao {

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private StockRowMapper stockRowMapper;

    @Override
    public Optional<Stock> get( final Long phoneId ) {
        String sqlQuery = "select * from stocks where phoneId = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sqlQuery, new Object[]{phoneId}, stockRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}