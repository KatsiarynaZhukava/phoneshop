package com.es.core.service;

import com.es.core.dto.output.CartTotalsOutputDto;
import com.es.core.exception.OutOfStockException;
import com.es.core.model.cart.Cart;

import java.util.Map;

public interface CartService {
    Cart getCart();
    void addPhone(Long phoneId, Long requestedQuantity) throws OutOfStockException;
    void update(Map<Long, Long> items) throws OutOfStockException;
    void remove(Long phoneId);
    CartTotalsOutputDto getCartTotalsOutputDto();
    CartTotalsOutputDto getCartTotalsOutputDto( final Map<Long, Long> cartItems );
}
