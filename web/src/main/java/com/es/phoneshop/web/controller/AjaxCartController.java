package com.es.phoneshop.web.controller;

import com.es.core.exception.OutOfStockException;
import com.es.core.service.CartService;
import com.es.phoneshop.web.dto.input.CartItemInputDto;
import com.es.phoneshop.web.dto.output.CartOutputDto;
import com.es.phoneshop.web.exception.InvalidInputException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Controller
@RequestMapping(value = "/ajaxCart")
public class AjaxCartController {
    @Resource
    private CartService cartService;

    @PostMapping
    @ResponseBody
    public CartOutputDto addPhone( final @RequestBody @Valid CartItemInputDto cartItemInputDto,
                                   final BindingResult bindingResult ) {
        if (bindingResult.hasErrors()) {
            throw new InvalidInputException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        try {
            cartService.addPhone(cartItemInputDto.getPhoneId(), cartItemInputDto.getRequestedQuantity());
        } catch (OutOfStockException e) {
            throw new InvalidInputException(e.getMessage());
        }
        return new CartOutputDto(cartService.getTotalQuantity(), cartService.getTotalCost());
    }
}