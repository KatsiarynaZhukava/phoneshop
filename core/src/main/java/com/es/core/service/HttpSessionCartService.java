package com.es.core.service;

import com.es.core.dao.PhoneDao;
import com.es.core.dao.StockDao;
import com.es.core.exception.NotFoundException;
import com.es.core.exception.OutOfStockException;
import com.es.core.model.cart.Cart;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@SessionScope
public class HttpSessionCartService implements CartService {
    public static final String STOCK_NOT_FOUND_BY_PHONE_ID = "Stock value not found by phoneId {0}";

    @Resource
    private PhoneDao phoneDao;
    @Resource
    private StockDao stockDao;

    private final Cart cart = new Cart();

    @Override
    public Cart getCart() {
        return cart;
    }

    @Override
    public void addPhone( final Long phoneId, final Long requestedQuantity ) throws OutOfStockException {
        Map<Long,Long> cartItems = cart.getItems();
        long quantityOfItemsInCart = cartItems.containsKey(phoneId) ? cartItems.get(phoneId) : 0;
        Long stock = stockDao.get(phoneId)
                             .orElseThrow(NotFoundException.supplier(STOCK_NOT_FOUND_BY_PHONE_ID, phoneId))
                             .getStock();
        if (stock < (quantityOfItemsInCart + requestedQuantity)) {
            throw new OutOfStockException( quantityOfItemsInCart + requestedQuantity, stock );
        }
        cartItems.put(phoneId, requestedQuantity + quantityOfItemsInCart);
        recalculateCart(cart);
    }

    @Override
    public void update( final Map<Long,Long> items ) throws OutOfStockException {
        Map<Long, Stock> stocks = stockDao.findAll( new ArrayList<>(items.keySet()))
                                          .stream()
                                          .collect( Collectors.toMap( stock -> stock.getPhone().getId(),
                                                                      Function.identity()) );
        for(Map.Entry<Long, Long> item : items.entrySet()) {
            Long itemKey = item.getKey();
            if (stocks.containsKey(itemKey)) {
                Long availableStock = stocks.get(itemKey).getStock();
                Long requestedStock = item.getValue();
                if (availableStock < requestedStock) {
                    throw new OutOfStockException(requestedStock, availableStock);
                }
            } else {
                throw new NotFoundException(STOCK_NOT_FOUND_BY_PHONE_ID, itemKey);
            }
        }
        cart.setItems(items);
        recalculateCart(cart);
    }

    @Override
    public void remove( final Long phoneId ) {
        cart.getItems().remove(phoneId);
        recalculateCart(cart);
    }

    private void recalculateCart( final Cart cart ) {
        cart.setTotalQuantity(cart.getItems().values().stream()
                                                      .reduce(Long::sum)
                                                      .orElse(0L));
        List<Phone> phones = phoneDao.findAll(new ArrayList<>(cart.getItems().keySet()));
        cart.setTotalCost(phones.stream()
                                .map(phone -> phone.getPrice()
                                                   .multiply(new BigDecimal(cart.getItems().get(phone.getId()))))
                                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}