package com.es.core.dao;

import com.es.core.model.phone.Stock;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StockDao {
    Optional<Stock> get(Long phoneId);
    List<Stock> findAll(List<Long> phoneIds);
    void update(Map<Long, Long> requestedStocks);
}
