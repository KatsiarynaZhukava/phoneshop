package com.es.phoneshop.web.controller.pages;

import com.es.core.dao.OrderDao;
import com.es.core.exception.NotFoundException;
import com.es.core.util.PhoneShopMessages;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/orderOverview")
public class OrderOverviewPageController {
    @Resource
    private OrderDao orderDao;

    @GetMapping(value = "/{orderId}")
    public String getOrder( final @PathVariable Long orderId,
                            final Model model ) {
        model.addAttribute("order", orderDao.get(orderId)
                                                        .orElseThrow( NotFoundException.supplier( PhoneShopMessages.ORDER_NOT_FOUND_BY_ID,
                                                                                                  orderId )));
        return "orderOverview";
    }
}
