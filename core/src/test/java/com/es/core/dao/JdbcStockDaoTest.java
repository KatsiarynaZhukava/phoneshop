package com.es.core.dao;

import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:context/applicationContext-core-test.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcStockDaoTest {

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

    private void assertStocksEquality( Stock expected, Stock actual ) {
        assertEquals(expected.getPhone().getId(), actual.getPhone().getId());
        assertEquals(expected.getStock(), actual.getStock());
        assertEquals(expected.getReserved(), actual.getReserved());
    }
}