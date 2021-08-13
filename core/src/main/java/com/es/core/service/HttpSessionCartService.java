package com.es.core.service;

import com.es.core.dao.PhoneDao;
import com.es.core.dao.StockDao;
import com.es.core.exception.NotFoundException;
import com.es.core.exception.OutOfStockException;
import com.es.core.model.cart.Cart;
import com.es.core.model.cart.CartItem;
import com.es.core.model.phone.Phone;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

@Service
@SessionScope
public class HttpSessionCartService implements CartService {
    public static final String PHONE_NOT_FOUND_BY_ID = "Phone not found by id: {0}";
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
        Phone phone = phoneDao.get(phoneId)
                              .orElseThrow(NotFoundException.supplier(PHONE_NOT_FOUND_BY_ID, phoneId));
        Map<Long,CartItem> cartItems = cart.getItems();
        long quantityOfItemsInCart = cartItems.containsKey(phoneId) ? cartItems.get(phoneId).getQuantity() : 0;
        Long stock = stockDao.get(phoneId)
                             .orElseThrow(NotFoundException.supplier(STOCK_NOT_FOUND_BY_PHONE_ID, phoneId))
                             .getStock();
        if (stock < (quantityOfItemsInCart + requestedQuantity)) {
            throw new OutOfStockException( quantityOfItemsInCart + requestedQuantity, stock );
        }
        cartItems.remove(phoneId);
        cartItems.put(phoneId, new CartItem(phone, requestedQuantity + quantityOfItemsInCart));
        recalculateCart(cart);
    }

    @Override
    public void update( final Map<Long,Long> items ) {

    }

    @Override
    public void remove( final Long phoneId ) {
        cart.getItems().remove(phoneId);
        recalculateCart(cart);
    }

    private void recalculateCart( final Cart cart ) {
        cart.setTotalQuantity(cart.getItems().values().stream()
                                                      .map(CartItem::getQuantity)
                                                      .mapToLong(quantity -> quantity).sum());
        cart.setTotalCost(cart.getItems().values().stream()
                                                  .map(cartItem -> cartItem.getPhone().getPrice().multiply(new BigDecimal(cartItem.getQuantity())))
                                                  .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}