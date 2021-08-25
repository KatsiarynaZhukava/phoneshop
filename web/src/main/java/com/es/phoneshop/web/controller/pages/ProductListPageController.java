package com.es.phoneshop.web.controller.pages;

import com.es.core.dao.PhoneDao;
import com.es.core.service.CartService;
import com.es.core.service.PaginationService;
import com.es.phoneshop.web.enums.SortField;
import com.es.phoneshop.web.enums.SortOrder;
import com.es.phoneshop.web.validation.ValueOfEnum;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.text.MessageFormat;

@Controller
@RequestMapping (value = "/productList")
@Validated
public class ProductListPageController {
    private static final int NUMBER_OF_PHONES_PER_PAGE = 10;
    private static final String PAGE_NOT_FOUND_MESSAGE = "Page not found by id {0}";

    @Resource
    private PhoneDao phoneDao;
    @Resource
    private CartService cartService;
    @Resource
    private PaginationService paginationService;

    @GetMapping
    public ModelAndView showProductList( final Model model,
                                         final @RequestParam(required = false, defaultValue = "1") int page,
                                         final @RequestParam(required = false) @ValueOfEnum(enumClass = SortField.class, name = "SortField") String sortField,
                                         final @RequestParam(required = false) @ValueOfEnum(enumClass = SortOrder.class, name = "SortOrder") String sortOrder,
                                         final @RequestParam(required = false) String searchQuery ) {
        long totalPagesNumber = paginationService.getTotalPagesNumber(NUMBER_OF_PHONES_PER_PAGE, searchQuery);

        if (page <= 0 || page > totalPagesNumber) {
            model.addAttribute("errorMessage", MessageFormat.format(PAGE_NOT_FOUND_MESSAGE, page));
            return new ModelAndView("error", model.asMap(), HttpStatus.NOT_FOUND);
        } else {
            model.addAttribute("phones", phoneDao.findAll( searchQuery, sortField, sortOrder,
                                                                       paginationService.getOffset(NUMBER_OF_PHONES_PER_PAGE, page),
                                                                       NUMBER_OF_PHONES_PER_PAGE));
            model.addAttribute("page", page);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortOrder", sortOrder);
            model.addAttribute("searchQuery", searchQuery);
            model.addAttribute("totalPagesNumber", totalPagesNumber);
            model.addAttribute("cart", cartService.getCartTotalsOutputDto());
        }
        return new ModelAndView("productList", model.asMap(), HttpStatus.OK);
    }
}