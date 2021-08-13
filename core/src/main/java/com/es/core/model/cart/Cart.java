package com.es.core.model.cart;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cart {
    private Map<Long, CartItem> items;
    private long totalQuantity;
    private BigDecimal totalCost;

    public Cart() {
        this.items = new ConcurrentHashMap<>();
    }

    public synchronized Map<Long, CartItem> getItems() {
        return items;
    }

    public synchronized void setItems(Map<Long, CartItem> items) {
        this.items = items;
    }

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
