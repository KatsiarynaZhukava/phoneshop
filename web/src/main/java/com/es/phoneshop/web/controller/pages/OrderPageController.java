package com.es.phoneshop.web.controller.pages;

import com.es.core.dto.input.UserInputDto;
import com.es.core.exception.OutOfStockException;
import com.es.core.model.order.Order;
import com.es.core.service.CartService;
import com.es.core.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping(value = "/order")
public class OrderPageController {
    @Resource
    private OrderService orderService;
    @Resource
    private CartService cartService;

    @GetMapping
    public String getOrder( final Model model,
                            final HttpServletResponse response ) {
        if (cartService.isEmpty()) return "redirect:/productList";

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        Order order = orderService.createOrder(cartService.getCart());
        model.addAttribute("order", order);
        if (!model.containsAttribute("userInputDto")) {
            model.addAttribute("userInputDto", new UserInputDto());
        }
        return "order";
    }

    @PostMapping
    public String placeOrder( @ModelAttribute("userInputDto") @Valid UserInputDto userInputDto,
                              final BindingResult bindingResult,
                              final RedirectAttributes redirectAttributes ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userInputDto", bindingResult);
            redirectAttributes.addFlashAttribute("userInputDto", userInputDto);
            return "redirect:/order";
        } else {
            Order order = orderService.createOrder(cartService.getCart(), userInputDto);
            try {
                orderService.placeOrder(order);
                cartService.clearCart();
                return "redirect:/orderOverview/" + order.getSecureId();
            } catch (OutOfStockException e) {
                redirectAttributes.addFlashAttribute("orderErrorMessage", "Some items in your cart ran out of stock and got removed");
                redirectAttributes.addFlashAttribute("userInputDto", userInputDto);
                return "redirect:/order";
            }
        }
    }
}