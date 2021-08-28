package com.es.phoneshop.web.controller.pages;

import com.es.core.dao.PhoneDao;
import com.es.core.service.CartService;
import com.es.core.exception.NotFoundException;
import com.es.core.util.PhoneShopMessages;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/productDetails")
public class ProductDetailsPageController {
    @Resource
    private PhoneDao phoneDao;
    @Resource
    private CartService cartService;

    @GetMapping("/{phoneId}")
    public String showProduct( final Model model,
                               final @PathVariable Long phoneId ) {
        model.addAttribute("phone", phoneDao.get(phoneId)
                                                        .orElseThrow(NotFoundException.supplier(PhoneShopMessages.PHONE_NOT_FOUND_BY_ID_MESSAGE, phoneId)));
        model.addAttribute("cart", cartService.getCartTotalsOutputDto());
        return "productDetails";
    }
}