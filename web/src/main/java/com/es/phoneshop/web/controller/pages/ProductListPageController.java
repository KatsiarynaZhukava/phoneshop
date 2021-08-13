package com.es.phoneshop.web.controller.pages;

import com.es.core.dao.PhoneDao;
import com.es.core.service.CartService;
import com.es.core.service.PaginationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.text.MessageFormat;

@Controller
@RequestMapping (value = "/productList")
public class ProductListPageController {
    private static final int NUMBER_OF_PHONES_PER_PAGE = 10;
    private static final String PAGE_INDEX_OUT_OF_BOUND = "Page index {0} out of bound";

    @Resource
    private PhoneDao phoneDao;
    @Resource
    private CartService cartService;
    @Resource
    private PaginationService paginationService;

    @GetMapping
    public String showProductList( final Model model,
                                   final @RequestParam(required = false, defaultValue = "1") int page,
                                   final @RequestParam(required = false) String sortField,
                                   final @RequestParam(required = false) String sortOrder,
                                   final @RequestParam(required = false) String searchQuery ) {
        long totalPagesNumber = paginationService.getTotalPagesNumber(NUMBER_OF_PHONES_PER_PAGE, searchQuery);
        if (page <= 0 || page > totalPagesNumber) {
            model.addAttribute("errorMessage", MessageFormat.format(PAGE_INDEX_OUT_OF_BOUND, page));
            return "error";
        }

        model.addAttribute("phones", phoneDao.findAll( searchQuery, sortField, sortOrder,
                                                          paginationService.getOffset(NUMBER_OF_PHONES_PER_PAGE, page),
                                                          NUMBER_OF_PHONES_PER_PAGE));
        model.addAttribute("page", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("totalPagesNumber", totalPagesNumber);
        model.addAttribute("totalPagesNumber", totalPagesNumber);
        model.addAttribute("cart", cartService.getCart());
        return "productList";
    }
}
