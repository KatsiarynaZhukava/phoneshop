package com.es.phoneshop.web.controller.pages;

import com.es.core.dao.PhoneDao;
import com.es.core.dto.input.CartInputDto;
import com.es.core.dto.input.CartItemInputDto;
import com.es.core.dto.output.DetailedCartOutputDto;
import com.es.core.exception.OutOfStockException;
import com.es.core.model.phone.Phone;
import com.es.core.service.CartService;
import com.es.phoneshop.web.validation.QuantityValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
    public String getCart( final Model model,
                           final HttpServletResponse response ) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        Map<Long, Long> copyCartItems = new HashMap<>(cartService.getCart().getItems());
        model.addAttribute("cart", cartService.getCartTotalsOutputDto());

        List<Phone> phones = phoneDao.findAll(new ArrayList<>(copyCartItems.keySet()));
        DetailedCartOutputDto detailedCartOutputDto =
                new DetailedCartOutputDto(phones.stream()
                                                .collect( Collectors.toMap( Function.identity(),
                                                                            phone -> copyCartItems.get(phone.getId()))));
        model.addAttribute("detailedCart", detailedCartOutputDto);

        if (!model.containsAttribute("cartInputDto")) {
            List<CartItemInputDto> items = new ArrayList<>();
            for (Map.Entry<Phone, Long> entry : detailedCartOutputDto.getItems().entrySet()) {
                items.add(new CartItemInputDto(entry.getKey().getId(), entry.getValue()));
            }
            model.addAttribute("cartInputDto", new CartInputDto(items));
        }
        return "cart";
    }

    @PutMapping
    public String updateCart( final @ModelAttribute("cartInputDto") @Valid CartInputDto cartInputDto,
                              final BindingResult bindingResult,
                              final RedirectAttributes redirectAttributes ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("updateMessage", "Error updating the cart");
        } else {
            Map<Long, Long> itemQuantities = cartInputDto.getItems()
                                                         .stream()
                                                         .collect( Collectors.toMap( CartItemInputDto::getPhoneId,
                                                                                     CartItemInputDto::getRequestedQuantity));
            try {
                cartService.update(itemQuantities);
            } catch (OutOfStockException e) {
                redirectAttributes.addFlashAttribute("updateMessage", "Error updating the cart");
                for ( OutOfStockException.OutOfStockItem item: e.getItems() ) {
                    int index = cartInputDto.getItems().indexOf(new CartItemInputDto(item.getPhoneId(), item.getStockRequested()));
                    bindingResult.rejectValue("items[" + index + "].requestedQuantity", "Stock exceeded",
                                              MessageFormat.format( OutOfStockException.DEFAULT_TEMPLATE_MESSAGE,
                                                                    item.getStockRequested(), item.getStockAvailable() ));
                }
            }
        }
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.cartInputDto", bindingResult);
        redirectAttributes.addFlashAttribute("cartInputDto", cartInputDto);
        return "redirect:/cart";
    }

    @DeleteMapping(value = "/{phoneId}")
    public String deletePhone( final @PathVariable Long phoneId ) {
        cartService.remove(phoneId);
        return "redirect:/cart";
    }
}