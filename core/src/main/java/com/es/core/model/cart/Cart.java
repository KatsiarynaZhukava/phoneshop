package com.es.core.model.cart;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cart {
    private final Map<Long, Long> items = new ConcurrentHashMap<>();
    public Map<Long, Long> getItems() { return items; }
}
