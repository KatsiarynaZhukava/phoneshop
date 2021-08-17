package com.es.core.service;

import com.es.core.dao.PhoneDao;
import com.es.core.dao.StockDao;
import com.es.core.exception.OutOfStockException;
import com.es.core.model.cart.Cart;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
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
                throw new OutOfStockException(quantityOfItemsInCart + requestedQuantity, stock);
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
            for(Map.Entry<Long, Long> item : items.entrySet()) {
                Long itemKey = item.getKey();
                Long requestedStock = item.getValue();

                if (stocks.containsKey(itemKey)) {
                    Long availableStock = stocks.get(itemKey).getStock();
                    if (availableStock < requestedStock) {
                        throw new OutOfStockException(requestedStock, availableStock);
                    } else {
                        cart.getItems().put(itemKey, requestedStock);
                    }
                } else {
                    throw new OutOfStockException(requestedStock, 0);
                }
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
    public Long getTotalQuantity() {
        return cart.getItems().values().stream()
                                       .reduce(Long::sum)
                                       .orElse(0L);
    }

    @Override
    public BigDecimal getTotalCost() {
        Map<Long, BigDecimal> phonePrices = phoneDao.findAll(new ArrayList<>(cart.getItems().keySet()))
                                                    .stream()
                                                    .collect(Collectors.toMap( Phone::getId, Phone::getPrice ));
        return cart.getItems().entrySet().stream()
                                         .map(entry -> new BigDecimal(entry.getValue()).multiply(phonePrices.get(entry.getKey())))
                                         .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}