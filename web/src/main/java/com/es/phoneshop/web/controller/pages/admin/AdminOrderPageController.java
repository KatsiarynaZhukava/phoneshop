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
                                     final @RequestParam OrderStatus orderStatus ) {
        Order order = orderDao.get(orderId)
                              .orElseThrow( NotFoundException.supplier( MessageFormat.format( PhoneShopMessages.ORDER_NOT_FOUND_BY_ID, orderId )));
        orderService.changeOrderStatus(order, orderStatus);
        return "redirect:/admin/orders/" + orderId;
    }
}