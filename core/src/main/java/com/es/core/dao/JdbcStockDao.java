package com.es.core.dao;

import com.es.core.model.phone.Stock;
import com.es.core.util.StockRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JdbcStockDao implements StockDao {
    private static final String SELECT_STOCK_BY_PHONE_ID_QUERY = "select * from stocks where phoneId = ?";
    private static final String SELECT_STOCKS_BY_PHONE_IDS_PART = "select * from stocks where phoneId in (";
    private static final String UPDATE_STOCKS_QUERY = "update stocks set stock = stock - ? where phoneId = ?";

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private StockRowMapper stockRowMapper;

    @Override
    public Optional<Stock> get( final Long phoneId ) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(SELECT_STOCK_BY_PHONE_ID_QUERY, new Object[]{phoneId}, stockRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Stock> findAll( final List<Long> phoneIds ) {
        StringBuilder sqlQuery = new StringBuilder(SELECT_STOCKS_BY_PHONE_IDS_PART);
        for(int i = 0; i < phoneIds.size(); i++) {
            sqlQuery.append("?,");
        }
        sqlQuery.replace(sqlQuery.length() - 1, sqlQuery.length(), ")");
        return jdbcTemplate.query(sqlQuery.toString(), phoneIds.toArray(), stockRowMapper);
    }

    @Override
    public void update( final Map<Long, Long> requestedStocks ) {
        List<Object[]> batch = new ArrayList<>();
        for (Map.Entry<Long, Long> phoneStock : requestedStocks.entrySet()) {
            Object[] values = new Object[]{ phoneStock.getValue(), phoneStock.getKey() };
            batch.add(values);
        }
        jdbcTemplate.batchUpdate(UPDATE_STOCKS_QUERY, batch);
    }
}