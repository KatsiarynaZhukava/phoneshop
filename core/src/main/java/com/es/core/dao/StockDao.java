package com.es.core.dao;

import com.es.core.model.phone.Stock;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StockDao {
    Optional<Stock> get(Long phoneId);
    List<Stock> findAll(List<Long> phoneIds);
    void increaseReserved(Map<Long, Long> requestedStocks);
    void decreaseReserved(Map<Long, Long> requestedStocks);
    void decreaseStock(Map<Long, Long> requestedStocks);
}
