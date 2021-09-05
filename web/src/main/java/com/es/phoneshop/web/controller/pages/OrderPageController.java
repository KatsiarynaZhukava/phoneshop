package com.es.phoneshop.web.controller.pages;

import com.es.core.dto.input.UserInputDto;
import com.es.core.exception.OutOfStockException;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderItem;
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
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/order")
public class OrderPageController {
    @Resource
    private OrderService orderService;
    @Resource
    private CartService cartService;

    @GetMapping
    public String getOrder( final Model model,
                            final HttpServletResponse response,
                            final HttpSession httpSession ) {
        if (cartService.isEmpty())  return "redirect:/productList";

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        Order order = orderService.createOrder(cartService.getCart());
        httpSession.setAttribute("currentOrder", order);
        model.addAttribute("order", order);
        if (!model.containsAttribute("userInputDto")) {
            model.addAttribute("userInputDto", new UserInputDto());
        }
        return "order";
    }

    @PostMapping
    public String placeOrder( final @ModelAttribute("userInputDto") @Valid UserInputDto userInputDto,
                              final BindingResult bindingResult,
                              final RedirectAttributes redirectAttributes,
                              final HttpSession httpSession ) {
        if (cartService.isEmpty()) return "redirect:/productList";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userInputDto", bindingResult);
            redirectAttributes.addFlashAttribute("userInputDto", userInputDto);
            return "redirect:/order";
        } else {
            Order order = (Order) httpSession.getAttribute("currentOrder");
            Map<Long,Long> cartItems;
            if (order == null) {
                cartItems = new HashMap<>(cartService.getCart().getItems());
                order = orderService.createOrder(cartService.getCart(), userInputDto);
            } else {
                cartItems = order.getOrderItems().stream()
                                                 .collect( Collectors.toMap( orderItem -> orderItem.getPhone().getId(),
                                                                             OrderItem::getQuantity ));
                orderService.fillUserInfo(order, userInputDto);
            }

            try {
                orderService.placeOrder(order);
                cartService.clearCart(cartItems);

                String orderUUID = UUID.randomUUID().toString();
                Map<String, Order> ordersWithUUIDsMap = (Map<String, Order>) httpSession.getAttribute("orders");
                if (ordersWithUUIDsMap == null) ordersWithUUIDsMap = new ConcurrentHashMap<>();
                ordersWithUUIDsMap.put(orderUUID, order);
                httpSession.setAttribute("orders", ordersWithUUIDsMap);

                return "redirect:/orderOverview/" + orderUUID;
            } catch (OutOfStockException e) {
                if (!cartService.isEmpty()) {
                    redirectAttributes.addFlashAttribute("orderErrorMessage", "Some items in your cart ran out of stock and got removed");
                    redirectAttributes.addFlashAttribute("userInputDto", userInputDto);
                } else {
                    redirectAttributes.addFlashAttribute("orderErrorMessage", "All items in your cart ran out of stock and got removed");
                    return "redirect:/productList";
                }
                return "redirect:/order";
            }
        }
    }
}