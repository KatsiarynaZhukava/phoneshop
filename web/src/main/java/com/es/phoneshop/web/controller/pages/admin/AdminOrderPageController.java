package com.es.phoneshop.web.controller.pages.admin;

import com.es.core.dao.OrderDao;
import com.es.core.exception.NotFoundException;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderStatus;
import com.es.core.service.OrderService;
import com.es.core.util.PhoneShopMessages;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.text.MessageFormat;

@Controller
@RequestMapping(value = "/admin/orders/{orderId}")
public class AdminOrderPageController {
    @Resource
    private OrderDao orderDao;
    @Resource
    private OrderService orderService;

    @GetMapping
    public String getOrder( final @PathVariable Long orderId,
                            final Model model ) {
        Order order = orderDao.get(orderId)
                              .orElseThrow( NotFoundException.supplier( MessageFormat.format( PhoneShopMessages.ORDER_NOT_FOUND_BY_ID, orderId )));
        model.addAttribute("order", order);
        return "admin/order";
    }

    @PostMapping
    public String changeOrderStatus( final @PathVariable Long orderId,
                                     final @RequestParam OrderStatus orderStatus,
                                     final RedirectAttributes redirectAttributes ) {
        Order order = orderDao.get(orderId)
                              .orElseThrow( NotFoundException.supplier( MessageFormat.format( PhoneShopMessages.ORDER_NOT_FOUND_BY_ID, orderId )));
        if (order.getStatus() == OrderStatus.NEW) {
            orderService.changeOrderStatus(order, orderStatus);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Further status changes are forbidden");
        }
        return "redirect:/admin/orders/" + orderId;
    }
}