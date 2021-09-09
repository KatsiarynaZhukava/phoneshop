package com.es.phoneshop.web.controller.pages.admin;

import com.es.core.dao.OrderDao;
import com.es.core.exception.NotFoundException;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderStatus;
import com.es.core.service.OrderService;
import com.es.core.util.PhoneShopMessages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    public ModelAndView changeOrderStatus( final @PathVariable Long orderId,
                                           final @RequestParam OrderStatus orderStatus,
                                           final HttpServletRequest request ) {
        Order order = orderDao.get(orderId)
                              .orElseThrow( NotFoundException.supplier( MessageFormat.format( PhoneShopMessages.ORDER_NOT_FOUND_BY_ID, orderId )));

        ModelAndView modelAndView = new ModelAndView();
        if (order.getStatus() == OrderStatus.NEW) {
            orderService.changeOrderStatus(order, orderStatus);
            modelAndView.setView(new RedirectView(request.getRequestURI()));
        } else {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            modelAndView.setViewName("admin/order");
            modelAndView.getModel().put("order", order);
            modelAndView.getModel().put("errorMessage", "Further status changes are forbidden");
        }
        return modelAndView;
    }
}