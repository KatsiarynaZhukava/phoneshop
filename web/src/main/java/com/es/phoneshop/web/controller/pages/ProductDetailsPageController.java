package com.es.phoneshop.web.controller.pages;

import com.es.core.dao.PhoneDao;
import com.es.core.service.CartService;
import com.es.phoneshop.web.dto.output.CartOutputDto;
import com.es.phoneshop.web.exception.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/productDetails")
public class ProductDetailsPageController {
    private static final String PHONE_NOT_FOUND_BY_ID = "Phone not found by id {0}";

    @Resource
    private PhoneDao phoneDao;
    @Resource
    private CartService cartService;

    @GetMapping("/{phoneId}")
    public String showProduct( final Model model,
                               final @PathVariable Long phoneId ) {
        model.addAttribute("phone", phoneDao.get(phoneId)
                                                        .orElseThrow(NotFoundException.supplier(PHONE_NOT_FOUND_BY_ID, phoneId)));
        model.addAttribute("cart", new CartOutputDto(cartService.getTotalQuantity(), cartService.getTotalCost()));
        return "productDetails";
    }
}