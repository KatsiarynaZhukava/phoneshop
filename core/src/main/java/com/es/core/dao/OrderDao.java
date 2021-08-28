package com.es.core.dao;

import com.es.core.model.order.Order;

import java.util.Optional;

public interface OrderDao {
    Optional<Order> get(Long key);
    void save(Order order);
    boolean exists( long id );
}