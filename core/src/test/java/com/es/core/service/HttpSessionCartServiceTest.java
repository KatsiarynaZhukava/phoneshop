package com.es.core.service;

import com.es.core.dao.PhoneDao;
import com.es.core.dao.StockDao;
import com.es.core.exception.NotFoundException;
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

import static org.junit.Assert.*;
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
    private Phone phone, anotherPhone;
    private Cart cart;

    @Test
    public void testGetEmptyCart() {
        Cart cart = cartService.getCart();
        assertNotNull(cart);
        assertNotNull(cart.getItems());
        assertNull(cart.getTotalCost());
        assertEquals(0L, cart.getTotalQuantity());
    }

    @Test(expected = NotFoundException.class)
    public void testAddNonexistentPhone() throws OutOfStockException {
        cartService.addPhone(-1L, 1L);
    }

    @Test
    public void testAddExistingPhone() throws OutOfStockException {
        initializeData();
        Cart cart = cartService.getCart();
        cartService.addPhone(phoneId1, 2L);
        assertEquals(1L, cart.getItems().size());
        assertEquals(2L, cart.getTotalQuantity());
        assertEquals(new BigDecimal(600), cart.getTotalCost());
        cartService.addPhone(phoneId1, 1L);
        assertEquals(1L, cart.getItems().size());
        assertEquals(3L, cart.getTotalQuantity());
        assertEquals(new BigDecimal(900), cart.getTotalCost());
        cartService.addPhone(phoneId2, 1L);
        assertEquals(2L, cart.getItems().size());
        assertEquals(4L, cart.getTotalQuantity());
        assertEquals(new BigDecimal(1400), cart.getTotalCost());
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
        assertEquals(1L, cart.getItems().size());
        assertEquals(1L, cart.getTotalQuantity());
        assertEquals(new BigDecimal(300), cart.getTotalCost());
    }

    @Test(expected = NotFoundException.class)
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
        assertEquals(1L, cart.getTotalQuantity());
        assertEquals(new BigDecimal(500), cart.getTotalCost());
        cartService.remove(phoneId1);
        assertEquals(1L, cart.getItems().size());
        assertEquals(1L, cart.getTotalQuantity());
        assertEquals(new BigDecimal(500), cart.getTotalCost());
        cartService.remove(phoneId2);
        assertEquals(0L, cart.getItems().size());
        assertEquals(0L, cart.getTotalQuantity());
        assertEquals(new BigDecimal(0), cart.getTotalCost());
    }

    public void initializeData() {
        phone = new Phone();
        phone.setId(phoneId1);
        phone.setPrice(new BigDecimal(300));

        Stock stock = new Stock();
        stock.setPhone(phone);
        stock.setStock(stock1);
        when(stockDao.get(phoneId1)).thenReturn(Optional.of(stock));

        anotherPhone = new Phone();
        anotherPhone.setId(phoneId2);
        anotherPhone.setPrice(new BigDecimal(500));

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
        Map<Long, Long> items = new HashMap<>();
        items.put(phoneId1, quantity1);
        items.put(phoneId2, quantity2);

        cart = cartService.getCart();
        cart.setItems(items);
        cart.setTotalQuantity(quantity1 + quantity2);
        cart.setTotalCost( phone.getPrice().multiply(BigDecimal.valueOf(quantity1)).add(
                           anotherPhone.getPrice().multiply(BigDecimal.valueOf(quantity2))));
    }
}