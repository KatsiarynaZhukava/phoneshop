package com.es.phoneshop.web.controller.pages;

import com.es.core.dao.PhoneDao;
import com.es.core.service.CartService;
import com.es.core.service.PaginationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceView;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(MockitoJUnitRunner.class)
public class ProductListPageControllerTest {
    @Mock
    private PaginationService paginationService;
    @Mock
    private PhoneDao phoneDao;
    @Mock
    private CartService cartService;

    private MockMvc mockMvc;
    private final String URL = "/productList";

    @InjectMocks
    private ProductListPageController controller;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .setSingleView(new InternalResourceView("/WEB-INF/pages/productList.jsp"))
                                 .build();
        when(paginationService.getOffset(10, 1)).thenReturn(0L);
        when(paginationService.getTotalPagesNumber(10, null)).thenReturn(100L);
        when(phoneDao.findAll( null, "price", "desc", 0L, 10)).thenReturn(new ArrayList<>());
    }

    @Test
    public void testShowProductList() throws Exception {
        mockMvc.perform(get(URL).param("page", "1")
                                .param("sortField", "price")
                                .param("sortOrder", "desc"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("productList"))
                                .andExpect(forwardedUrl("/WEB-INF/pages/productList.jsp"))
                                .andExpect(model().attributeExists("phones"))
                                .andExpect(model().attributeExists("page"))
                                .andExpect(model().attributeExists("totalPagesNumber"))
                                .andExpect(model().attributeExists("sortField"))
                                .andExpect(model().attributeExists("sortOrder"))
                                .andExpect(model().attributeDoesNotExist("searchQuery"))
                                .andExpect(model().attributeExists("cart"));

        verify(paginationService, times(1)).getOffset(10, 1);
        verify(paginationService, times(1)).getTotalPagesNumber(10, null);
        verifyNoMoreInteractions(paginationService);

        verify(phoneDao, times(1)).findAll( null, "price", "desc", 0L, 10);
        verifyNoMoreInteractions(phoneDao);
    }
}