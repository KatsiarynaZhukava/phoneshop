package com.es.core.service;

import com.es.core.dao.PhoneDao;
import com.es.core.dao.StockDao;
import com.es.core.dto.output.CartTotalsOutputDto;
import com.es.core.exception.OutOfStockException;
import com.es.core.exception.OutOfStockItem;
import com.es.core.model.cart.Cart;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@SessionScope
public class HttpSessionCartService implements CartService {

    @Resource
    private PhoneDao phoneDao;
    @Resource
    private StockDao stockDao;

    private final Lock lock = new ReentrantLock();
    private final Cart cart = new Cart();

    @Override
    public Cart getCart() {
        return cart;
    }

    @Override
    public void addPhone( final Long phoneId, final Long requestedQuantity ) throws OutOfStockException {
        Long stock = stockDao.get(phoneId)
                             .orElse(new Stock(null, 0L, 0L))
                             .getStock();
        lock.lock();
        try {
            Map<Long, Long> cartItems = cart.getItems();
            long quantityOfItemsInCart = cartItems.getOrDefault(phoneId, 0L);
            if (stock < (quantityOfItemsInCart + requestedQuantity)) {
                throw new OutOfStockException(phoneId, quantityOfItemsInCart + requestedQuantity, stock);
            }
            cartItems.put(phoneId, requestedQuantity + quantityOfItemsInCart);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update( final Map<Long,Long> items ) throws OutOfStockException {
        Map<Long, Stock> stocks = stockDao.findAll( new ArrayList<>(items.keySet()))
                                          .stream()
                                          .collect( Collectors.toMap( stock -> stock.getPhone().getId(),
                                                                      Function.identity()) );
        lock.lock();
        try {
            List<OutOfStockItem> outOfStockItems = new ArrayList<>();
            for(Map.Entry<Long, Long> item : items.entrySet()) {
                Long itemKey = item.getKey();
                Long stockRequested = item.getValue();

                if (stocks.containsKey(itemKey)) {
                    Long stockAvailable = stocks.get(itemKey).getStock();
                    if (stockAvailable < stockRequested) {
                        outOfStockItems.add(new OutOfStockItem(itemKey, stockRequested, stockAvailable));
                    }
                } else {
                    outOfStockItems.add(new OutOfStockItem(itemKey, stockRequested, 0L));
                }
            }
            if (outOfStockItems.isEmpty()) {
                cart.getItems().putAll(items);
            } else {
                throw new OutOfStockException(outOfStockItems);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove( final Long phoneId ) {
        lock.lock();
        try {
            cart.getItems().remove(phoneId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public CartTotalsOutputDto getCartTotalsOutputDto() {
        Map<Long, Long> copyCartItems = new HashMap<>(cart.getItems());
        return getCartTotalsOutputDto(copyCartItems);
    }

    @Override
    public CartTotalsOutputDto getCartTotalsOutputDto( final Map<Long, Long> cartItems ) {
        Long totalQuantity = cartItems.values().stream()
                                               .reduce(Long::sum)
                                               .orElse(0L);
        BigDecimal totalCost = getTotalCost(cartItems);
        return new CartTotalsOutputDto(totalQuantity, totalCost);
    }

    @Override
    public BigDecimal getTotalCost( final Map<Long, Long> cartItems ) {
        Map<Long, BigDecimal> phonePrices = phoneDao.findAll(new ArrayList<>(cartItems.keySet()))
                                                    .stream()
                                                    .collect(Collectors.toMap( Phone::getId, Phone::getPrice ));
        return cartItems.entrySet().stream()
                                   .map(entry -> new BigDecimal(entry.getValue()).multiply(phonePrices.get(entry.getKey())))
                                   .reduce( BigDecimal.ZERO, BigDecimal::add );
    }

    @Override
    public void clearCart( final Map<Long, Long> cartItems ) {
        lock.lock();
        try {
            Map<Long, Long> currentCartItems = cart.getItems();

            for(Map.Entry<Long, Long> cartItem: cartItems.entrySet()) {
                Long itemKey = cartItem.getKey();
                if (currentCartItems.containsKey(itemKey)) {
                    Long stockDelta = currentCartItems.get(itemKey) - cartItem.getValue();
                    if (stockDelta > 0) {
                        currentCartItems.put(itemKey, stockDelta);
                    } else {
                        currentCartItems.remove(itemKey);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        return cart.getItems().isEmpty();
    }
}