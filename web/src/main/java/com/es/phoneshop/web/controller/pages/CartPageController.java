package com.es.phoneshop.web.controller.pages;

import com.es.core.dao.PhoneDao;
import com.es.core.exception.OutOfStockException;
import com.es.core.model.cart.Cart;
import com.es.core.model.phone.Phone;
import com.es.core.service.CartService;
import com.es.phoneshop.web.dto.input.CartInputDto;
import com.es.phoneshop.web.dto.input.CartItemInputDto;
import com.es.phoneshop.web.dto.output.CartOutputDto;
import com.es.phoneshop.web.dto.output.DetailedCartOutputDto;
import com.es.phoneshop.web.exception.InvalidInputException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/cart")
public class CartPageController {
    @Resource
    private PhoneDao phoneDao;
    @Resource
    private CartService cartService;

    @GetMapping
    public String getCart( final Model model ) {
        Cart cart = cartService.getCart();
        model.addAttribute("cart", new CartOutputDto(cartService.getTotalQuantity(), cartService.getTotalCost()));

        List<Phone> phones = phoneDao.findAll(new ArrayList<>(cart.getItems().keySet()));
        DetailedCartOutputDto detailedCartOutputDto = new DetailedCartOutputDto();
        detailedCartOutputDto.setItems(phones.stream()
                                             .collect( Collectors.toMap( Function.identity(),
                                                                         phone -> cart.getItems().get(phone.getId()))));
        model.addAttribute("detailedCart", detailedCartOutputDto);

        List<CartItemInputDto> items = new ArrayList<>();
        for (Map.Entry<Phone, Long> entry: detailedCartOutputDto.getItems().entrySet()) {
            items.add(new CartItemInputDto(entry.getKey().getId(), entry.getValue()));
        }
        model.addAttribute("cartInputDto", new CartInputDto(items));
        return "cart";
    }

    @PostMapping
    public String updateCart( final @Valid CartInputDto cartInputDto,
                              final BindingResult bindingResult ) throws OutOfStockException {
        if (bindingResult.hasErrors()) {
            throw new InvalidInputException();
        }
        Map<Long, Long> items = cartInputDto.getItems()
                                            .stream()
                                            .collect( Collectors.toMap( CartItemInputDto::getPhoneId,
                                                                        CartItemInputDto::getRequestedQuantity) );
        cartService.update(items);
        return "redirect:/cart";
    }
}