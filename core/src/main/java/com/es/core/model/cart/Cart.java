package com.es.core.model.cart;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cart {
    private Map<Long, Long> items;
    private long totalQuantity;
    private BigDecimal totalCost;

    public Cart() {
        this.items = new ConcurrentHashMap<>();
    }

    public Map<Long, Long> getItems() { return items; }

    public void setItems(Map<Long, Long> items) { this.items = items; }

    public synchronized long getTotalQuantity() {
        return totalQuantity;
    }

    public synchronized void setTotalQuantity(long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public synchronized BigDecimal getTotalCost() {
        return totalCost;
    }

    public synchronized void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
