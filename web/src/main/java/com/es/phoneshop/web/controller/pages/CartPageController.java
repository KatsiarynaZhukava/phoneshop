package com.es.phoneshop.web.controller.pages;

import com.es.core.dao.PhoneDao;
import com.es.core.dao.StockDao;
import com.es.core.exception.OutOfStockException;
import com.es.core.model.cart.Cart;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
import com.es.core.service.CartService;
import com.es.phoneshop.web.dto.input.CartInputDto;
import com.es.phoneshop.web.dto.input.CartItemInputDto;
import com.es.phoneshop.web.dto.output.CartOutputDto;
import com.es.phoneshop.web.dto.output.DetailedCartOutputDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
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
    private StockDao stockDao;
    @Resource
    private CartService cartService;

    @GetMapping
    public String getCart( final Model model ) {
        Cart cart = cartService.getCart();
        model.addAttribute("cart", new CartOutputDto(cartService.getTotalQuantity(), cartService.getTotalCost()));

        List<Phone> phones = phoneDao.findAll(new ArrayList<>(cart.getItems().keySet()));
        DetailedCartOutputDto detailedCartOutputDto =
                new DetailedCartOutputDto( phones.stream()
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

    @PutMapping
    public String updateCart( final @Valid CartInputDto cartInputDto,
                              final BindingResult bindingResult,
                              final Model model ) {
        tryToUpdate(cartInputDto, bindingResult);
        setModelAttributes(model, cartInputDto);
        return "cart";
    }

    private void tryToUpdate( final CartInputDto cartInputDto,
                              final BindingResult bindingResult ) {
        try {
            cartService.update(cartInputDto.getItems()
                    .stream()
                    .collect(Collectors.toMap(CartItemInputDto::getPhoneId,
                            CartItemInputDto::getRequestedQuantity)));
        } catch (OutOfStockException e) {
            List<CartItemInputDto> items = new ArrayList<>(cartInputDto.getItems());
            for (int i = 0; i < items.size(); i++) {
                CartItemInputDto item = items.get(i);
                Long stockRequested = item.getRequestedQuantity();
                Long stockAvailable = stockDao.get(item.getPhoneId()).map(Stock::getStock).orElse(0L);
                if (stockRequested > stockAvailable) {
                    bindingResult.rejectValue("items[" + i + "].requestedQuantity", "Stock exceeded",
                            MessageFormat.format("The overall requested stock {0} exceeds the available {1}",
                                    stockRequested, stockAvailable ));
                }
            }
        }
    }

    private void setModelAttributes( final Model model,
                                     final CartInputDto cartInputDto ) {
        Cart cart = cartService.getCart();
        model.addAttribute("cart", new CartOutputDto(cartService.getTotalQuantity(), cartService.getTotalCost()));
        List<Phone> phones = phoneDao.findAll(new ArrayList<>(cart.getItems().keySet()));
        Map<Long, Long> itemQuantities = cartInputDto.getItems()
                                                     .stream()
                                                     .collect( Collectors.toMap( CartItemInputDto::getPhoneId,
                                                                                 CartItemInputDto::getRequestedQuantity));
        DetailedCartOutputDto detailedCartOutputDto =
                new DetailedCartOutputDto( phones.stream()
                                                 .collect( Collectors.toMap( Function.identity(),
                                                                             phone -> itemQuantities.get(phone.getId()))));
        model.addAttribute("detailedCart", detailedCartOutputDto);
        model.addAttribute("cartInputDto", cartInputDto);
    }
}