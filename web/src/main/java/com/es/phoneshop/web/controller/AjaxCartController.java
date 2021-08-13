package com.es.phoneshop.web.controller;

import com.es.core.exception.OutOfStockException;
import com.es.core.model.cart.Cart;
import com.es.core.service.CartService;
import com.es.phoneshop.web.controller.dto.CartItemInputDto;
import com.es.phoneshop.web.controller.dto.CartOutputDto;
import com.es.phoneshop.web.exception.InvalidInputException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;

@Controller
@RequestMapping(value = "/ajaxCart")
public class AjaxCartController {
    @Resource
    private CartService cartService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CartOutputDto addPhone( final @RequestBody @Valid CartItemInputDto cartItemInputDto,
                                   final BindingResult bindingResult ) {
        if (bindingResult.hasErrors()) {
            throw new InvalidInputException();
        }
        try {
            cartService.addPhone(cartItemInputDto.getPhoneId(), cartItemInputDto.getRequestedQuantity());
        } catch (OutOfStockException e) {

        }
        Cart cart = cartService.getCart();
        return new CartOutputDto(cart.getTotalQuantity(), cart.getTotalCost());
    }
}
