package com.es.phoneshop.web.controller.pages.admin;

import com.es.core.dao.OrderDao;
import com.es.core.model.order.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/orders")
public class OrdersPageController {
    @Resource
    private OrderDao orderDao;

    @GetMapping
    public String getOrders( final Model model ) {
        List<Order> orders = orderDao.findAll();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }
}
