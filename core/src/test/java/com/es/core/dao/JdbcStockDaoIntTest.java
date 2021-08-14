package com.es.core.dao;

import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:context/applicationContext-core-test.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcStockDaoIntTest {

    @Resource
    private StockDao stockDao;

    @Test
    public void testGetExistingPhoneStock() {
        Long phoneId = 2000L;
        Optional<Stock> stockFromDb = stockDao.get(phoneId);
        Stock expected = new Stock(new Phone(), 10L, 0L);
        expected.getPhone().setId(phoneId);

        assertTrue(stockFromDb.isPresent());
        assertStocksEquality(expected, stockFromDb.get());
    }

    @Test
    public void testGetNonexistentPhoneStock() {
        assertFalse(stockDao.get(-1L).isPresent());
    }

    @Test
    public void testFindAllNonexistentStocks() {
        List<Long> phoneIds = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
        List<Stock> stocks = stockDao.findAll(phoneIds);
        assertEquals(0L, stocks.size());
    }

    @Test
    public void testFindAllExistingStocks() {
        List<Long> phoneIds = new ArrayList<>(Arrays.asList(2001L, 2002L, 2003L));
        List<Stock> stocks = stockDao.findAll(phoneIds);
        assertEquals(3L, stocks.size());
        assertEquals(2001L, stocks.get(0).getPhone().getId().longValue());
        assertEquals(2002L, stocks.get(1).getPhone().getId().longValue());
        assertEquals(2003L, stocks.get(2).getPhone().getId().longValue());
        assertEquals(9L, stocks.get(0).getStock().longValue());
        assertEquals(28L, stocks.get(1).getStock().longValue());
        assertEquals(29L, stocks.get(2).getStock().longValue());
        assertEquals(7L, stocks.get(0).getReserved().longValue());
        assertEquals(6L, stocks.get(1).getReserved().longValue());
        assertEquals(7L, stocks.get(2).getReserved().longValue());
    }

    private void assertStocksEquality( Stock expected, Stock actual ) {
        assertEquals(expected.getPhone().getId(), actual.getPhone().getId());
        assertEquals(expected.getStock(), actual.getStock());
        assertEquals(expected.getReserved(), actual.getReserved());
    }
}