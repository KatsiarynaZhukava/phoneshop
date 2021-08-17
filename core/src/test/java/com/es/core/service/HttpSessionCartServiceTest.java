package com.es.core.service;

import com.es.core.dao.PhoneDao;
import com.es.core.dao.StockDao;
import com.es.core.exception.OutOfStockException;
import com.es.core.model.cart.Cart;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class HttpSessionCartServiceTest {
    @Mock
    private PhoneDao phoneDao;
    @Mock
    private StockDao stockDao;
    @InjectMocks
    private HttpSessionCartService cartService;

    private final Long phoneId1 = 2000L, phoneId2 = 2001L;
    private final Long stock1 = 10L, stock2 = 1L;
    private final Long price1 = 300L, price2 = 500L;
    private Phone phone, anotherPhone;
    private Cart cart;

    @Test
    public void testGetEmptyCart() {
        Cart cart = cartService.getCart();
        assertNotNull(cart);
        assertNotNull(cart.getItems());
        assertEquals(BigDecimal.ZERO, cartService.getTotalCost());
        assertEquals(0L, cartService.getTotalQuantity().longValue());
    }

    @Test(expected = OutOfStockException.class)
    public void testAddNonexistentPhone() throws OutOfStockException {
        cartService.addPhone(-1L, 1L);
    }

    @Test
    public void testAddExistingPhone() throws OutOfStockException {
        initializeData();
        Cart cart = cartService.getCart();
        cartService.addPhone(phoneId1, 2L);
        assertEquals(1L, cart.getItems().size());
        assertEquals(2L, cartService.getTotalQuantity().longValue());
        assertEquals(new BigDecimal(price1 * 2), cartService.getTotalCost());
        cartService.addPhone(phoneId1, 1L);
        assertEquals(1L, cart.getItems().size());
        assertEquals(3L, cartService.getTotalQuantity().longValue());
        assertEquals(new BigDecimal(price1 * 3), cartService.getTotalCost());
        cartService.addPhone(phoneId2, 1L);
        assertEquals(2L, cart.getItems().size());
        assertEquals(4L, cartService.getTotalQuantity().longValue());
        assertEquals(new BigDecimal(price1 * 3 + price2), cartService.getTotalCost());
    }

    @Test(expected = OutOfStockException.class)
    public void testAddPhoneNotInCartQuantityExceedsStock() throws OutOfStockException {
        initializeData();
        cartService.addPhone(phoneId1, 1000L);
    }

    @Test(expected = OutOfStockException.class)
    public void testAddPhoneAlreadyInCartQuantityExceedsStock() throws OutOfStockException {
        initializeData();
        cartService.addPhone(phoneId2, 1L);
        cartService.addPhone(phoneId2, 1L);
    }

    @Test
    public void testUpdate() throws OutOfStockException {
        initializeCart();

        Map<Long, Long> items = new HashMap<>();
        items.put(phoneId1, 1L);
        cartService.update(items);
        assertEquals(2L, cart.getItems().size());
        assertEquals(2L, cartService.getTotalQuantity().longValue());
        assertEquals(new BigDecimal(price1 + price2), cartService.getTotalCost());
    }

    @Test(expected = OutOfStockException.class)
    public void testUpdateNonexistentPhone() throws OutOfStockException {
        Map<Long, Long> items = new HashMap<>();
        items.put(1L, 1L);
        cartService.update(items);
    }

    @Test(expected = OutOfStockException.class)
    public void testUpdateExistingPhoneQuantityExceedsStock() throws OutOfStockException {
        initializeData();
        Map<Long, Long> items = new HashMap<>();
        items.put(phoneId1, 1000L);
        cartService.update(items);
    }

    @Test
    public void testRemoveItemsFromCart() {
        initializeCart();
        cartService.remove(phoneId1);
        assertEquals(1L, cart.getItems().size());
        assertEquals(1L, cartService.getTotalQuantity().longValue());
        assertEquals(new BigDecimal(price2), cartService.getTotalCost());
        cartService.remove(phoneId1);
        assertEquals(1L, cart.getItems().size());
        assertEquals(1L, cartService.getTotalQuantity().longValue());
        assertEquals(new BigDecimal(price2), cartService.getTotalCost());
        cartService.remove(phoneId2);
        assertEquals(0L, cart.getItems().size());
        assertEquals(0L, cartService.getTotalQuantity().longValue());
        assertEquals(new BigDecimal(0), cartService.getTotalCost());
    }

    public void initializeData() {
        phone = new Phone();
        phone.setId(phoneId1);
        phone.setPrice(new BigDecimal(price1));

        Stock stock = new Stock();
        stock.setPhone(phone);
        stock.setStock(stock1);
        when(stockDao.get(phoneId1)).thenReturn(Optional.of(stock));

        anotherPhone = new Phone();
        anotherPhone.setId(phoneId2);
        anotherPhone.setPrice(new BigDecimal(price2));

        Stock anotherStock = new Stock();
        anotherStock.setPhone(anotherPhone);
        anotherStock.setStock(stock2);
        when(stockDao.get(phoneId2)).thenReturn(Optional.of(anotherStock));

        when(phoneDao.findAll(Collections.singletonList(phoneId1))).thenReturn(Collections.singletonList(phone));
        when(phoneDao.findAll(Collections.singletonList(phoneId2))).thenReturn(Collections.singletonList(anotherPhone));
        when(phoneDao.findAll(Arrays.asList(phoneId1, phoneId2))).thenReturn(Arrays.asList(phone, anotherPhone));

        when(stockDao.findAll(Collections.singletonList(phoneId1))).thenReturn(Collections.singletonList(stock));
    }

    private void initializeCart() {
        initializeData();
        Long quantity1 = 2L, quantity2 = 1L;
        cart = cartService.getCart();
        Map<Long, Long> items = cart.getItems();
        items.put(phoneId1, quantity1);
        items.put(phoneId2, quantity2);
    }
}