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
import com.es.phoneshop.web.validation.QuantityValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.MessageFormat;
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
    @Resource
    private QuantityValidator quantityValidator;

    @InitBinder("cartInputDto")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(quantityValidator);
    }

    @GetMapping
    public String getCart( final Model model, final HttpServletResponse response ) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        Cart cart = cartService.getCart();
        model.addAttribute("cart", new CartOutputDto(cartService.getTotalQuantity(), cartService.getTotalCost()));

        List<Phone> phones = phoneDao.findAll(new ArrayList<>(cart.getItems().keySet()));
        DetailedCartOutputDto detailedCartOutputDto =
                new DetailedCartOutputDto(phones.stream()
                                                .collect( Collectors.toMap( Function.identity(),
                                                                            phone -> cart.getItems().get(phone.getId()))));
        model.addAttribute("detailedCart", detailedCartOutputDto);

        List<CartItemInputDto> items = new ArrayList<>();
        for (Map.Entry<Phone, Long> entry : detailedCartOutputDto.getItems().entrySet()) {
            items.add(new CartItemInputDto(entry.getKey().getId(), entry.getValue()));
        }
        model.addAttribute("cartInputDto", new CartInputDto(items));
        return "cart";
    }

    @PutMapping
    public String updateCart( final @ModelAttribute("cartInputDto") @Valid CartInputDto cartInputDto,
                              final BindingResult bindingResult,
                              final Model model ) {
        Map<Long, Long> itemQuantities = cartInputDto.getItems()
                                                     .stream()
                                                     .collect( Collectors.toMap( CartItemInputDto::getPhoneId,
                                                               CartItemInputDto::getRequestedQuantity));
        if (bindingResult.hasErrors()) {
            model.addAttribute("updateMessage", "Error updating the cart");
        } else {
            try {
                cartService.update(itemQuantities);
            } catch (OutOfStockException e) {
                model.addAttribute("updateMessage", "Error updating the cart");
                int index = cartInputDto.getItems().indexOf(new CartItemInputDto(e.getPhoneId(), e.getStockRequested()));
                bindingResult.rejectValue("items[" + index + "].requestedQuantity", "Stock exceeded",
                                            MessageFormat.format("The overall requested stock {0} exceeds the available {1}",
                                                                 e.getStockRequested(), e.getStockAvailable() ));
            }
        }
        Cart cart = cartService.getCart();
        model.addAttribute("cart", new CartOutputDto(cartService.getTotalQuantity(), cartService.getTotalCost()));

        List<Phone> phones = phoneDao.findAll(new ArrayList<>(cart.getItems().keySet()));
        DetailedCartOutputDto detailedCartOutputDto =
                new DetailedCartOutputDto( phones.stream()
                                                 .collect( Collectors.toMap( Function.identity(),
                                                                             phone -> itemQuantities.get(phone.getId()))));
        model.addAttribute("detailedCart", detailedCartOutputDto);
        model.addAttribute("cartInputDto", cartInputDto);
        return "cart";
    }

    @PostMapping(value = "/{phoneId}")
    public String deletePhone( final @PathVariable Long phoneId ) {
        cartService.remove(phoneId);
        return "redirect:/cart";
    }
}