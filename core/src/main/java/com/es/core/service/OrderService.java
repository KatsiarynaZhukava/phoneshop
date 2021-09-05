package com.es.core.service;

import com.es.core.dto.input.UserInputDto;
import com.es.core.exception.OutOfStockException;
import com.es.core.model.cart.Cart;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderStatus;

public interface OrderService {
    Order createOrder(Cart cart);
    Order createOrder(Cart cart, UserInputDto userInputDto);
    void fillUserInfo(Order order, UserInputDto userInputDto);
    void placeOrder(Order order) throws OutOfStockException;
    void changeOrderStatus(Order order, OrderStatus orderStatus);
}
