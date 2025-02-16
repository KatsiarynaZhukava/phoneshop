package com.es.phoneshop.web.controller;

import com.es.core.dto.input.CartItemInputDto;
import com.es.core.dto.output.CartTotalsOutputDto;
import com.es.core.exception.OutOfStockException;
import com.es.core.service.CartService;
import com.es.phoneshop.web.exception.InvalidInputException;
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

    @PostMapping
    @ResponseBody
    public CartTotalsOutputDto addPhone(final @RequestBody @Valid CartItemInputDto cartItemInputDto,
                                        final BindingResult bindingResult ) {
        if (bindingResult.hasErrors()) {
            throw new InvalidInputException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        try {
            cartService.addPhone(cartItemInputDto.getPhoneId(), cartItemInputDto.getRequestedQuantity());
        } catch (OutOfStockException e) {
            throw new InvalidInputException(e.getMessage());
        }
        return cartService.getCartTotalsOutputDto();
    }
}