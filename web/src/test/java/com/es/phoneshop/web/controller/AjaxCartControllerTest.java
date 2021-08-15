package com.es.phoneshop.web.controller;

import com.es.core.exception.OutOfStockException;
import com.es.core.model.cart.Cart;
import com.es.core.service.CartService;
import com.es.phoneshop.web.controller.exceptionHandlers.InvalidInputControllerAdvice;
import com.es.phoneshop.web.exception.InvalidInputException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class AjaxCartControllerTest {
    @Mock
    private CartService cartService;

    private MockMvc mockMvc;

    private Cart cart = new Cart();
    private Long quantity = 42L, phoneId = 2000L;
    private final String URL = "/ajaxCart";

    @InjectMocks
    private AjaxCartController ajaxCartController;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(ajaxCartController)
                                 .setControllerAdvice(new InvalidInputControllerAdvice())
                                 .build();
        when(cartService.getCart()).thenReturn(cart);
    }


    @Test
    public void testAddPhone() throws Exception {
        BigDecimal totalCost = new BigDecimal(4200);
        doAnswer((Answer<Void>) invocation -> {
                           cart.getItems().put(phoneId, quantity);
                           cart.setTotalCost(totalCost);
                           cart.setTotalQuantity(quantity);
                           return null;
        }).when(cartService).addPhone(phoneId, quantity);
        String jsonBody = "{\"phoneId\": \"" + phoneId + "\", \"requestedQuantity\": \"" + quantity + "\"}";

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                                 .content(jsonBody))
                                 .andExpect(status().isOk())
                                 .andExpect(content().contentType("application/json;charset=UTF-8"))
                                 .andExpect(jsonPath("$.totalQuantity").value(quantity))
                                 .andExpect(jsonPath("$.totalCost").value(totalCost));
    }

    @Test
    public void testAddPhoneQuantityExceedsStock() throws Exception {
        Long availableStock = 10L;
        doThrow(new OutOfStockException(quantity, availableStock)).when(cartService).addPhone(phoneId, quantity);
        cart.setItems(new HashMap<>());

        String jsonBody = "{\"phoneId\": \"" + phoneId + "\", \"requestedQuantity\": \"" + quantity + "\"}";

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                                 .content(jsonBody))
                                 .andExpect(status().isBadRequest())
                                 .andExpect(result -> assertEquals( result.getResolvedException().getClass(),
                                                                    InvalidInputException.class) )
                                 .andExpect(content().string("{\"message\":\"The overall requested stock " + quantity +
                                         " exceeds the available " + availableStock +"\"}"));
    }
}