package com.es.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:context/applicationContext-core-test.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PaginationServiceTest {
    @Resource
    private PaginationService paginationService;

    @Test
    public void testGetOffset() {
        assertEquals(0L, paginationService.getOffset(10, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOffsetNegativeLimit() {
        paginationService.getOffset(-1, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOffsetZeroLimit() {
        paginationService.getOffset(0, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOffsetNegativeCurrentPage() {
        paginationService.getOffset(10, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOffsetZeroCurrentPage() {
        assertEquals(0L, paginationService.getOffset(10, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTotalPagesNumberNegativeLimit() {
        paginationService.getTotalPagesNumber(-1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTotalPagesNumberZeroLimit() {
        paginationService.getTotalPagesNumber(0, null);
    }

    @Test
    public void testGetTotalPagesNumberWithQueryFound() {
        assertEquals(1L, paginationService.getTotalPagesNumber(10, "Lenovo"));
        assertEquals(2L, paginationService.getTotalPagesNumber(2, "Alcatel"));
    }

    @Test
    public void testGetTotalPagesNumberWithQueryNotFound() {
        assertEquals(1L, paginationService.getTotalPagesNumber(1, "Blabla"));
    }
}