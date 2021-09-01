package com.es.phoneshop.web.controller.pages;

import com.es.core.exception.NotFoundException;
import com.es.core.model.order.Order;
import com.es.core.util.PhoneShopMessages;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping(value = "/orderOverview")
public class OrderOverviewPageController {

    @GetMapping(value = "/{orderUUID}")
    public String getOrder( final @PathVariable String orderUUID,
                            final Model model,
                            final HttpSession httpSession ) {
        Map<String, Order> ordersWithUUIDsMap = (Map<String, Order>) httpSession.getAttribute("orders");
        if (ordersWithUUIDsMap != null && !ordersWithUUIDsMap.isEmpty()) {
            Order order = ordersWithUUIDsMap.get(orderUUID);
            if (order != null) {
                model.addAttribute("order", order);
            } else {
                throw new NotFoundException(PhoneShopMessages.ORDER_NOT_FOUND_BY_ID, orderUUID);
            }
        } else {
            throw new NotFoundException(PhoneShopMessages.ORDERS_NOT_FOUND_IN_SESSION);
        }
        return "orderOverview";
    }
}