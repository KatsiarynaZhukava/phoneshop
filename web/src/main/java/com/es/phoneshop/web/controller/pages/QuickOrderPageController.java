package com.es.phoneshop.web.controller.pages;

import com.es.core.dao.PhoneDao;
import com.es.core.dto.input.QuickOrderInputDto;
import com.es.core.dto.input.QuickOrderItemInputDto;
import com.es.core.exception.OutOfStockException;
import com.es.core.model.phone.Phone;
import com.es.core.service.CartService;
import com.es.core.service.QuickOrderService;
import com.es.phoneshop.web.validation.QuickOrderValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/quickOrder")
public class QuickOrderPageController {
    @Resource
    private PhoneDao phoneDao;
    @Resource
    private CartService cartService;
    @Resource
    private QuickOrderService quickOrderService;
    @Resource
    private QuickOrderValidator quickOrderValidator;

    @InitBinder("quickOrderInputDto")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(quickOrderValidator);
    }

    @GetMapping
    public String showQuickOrderPage( final Model model ) {
        model.addAttribute("quickOrderInputDto", new QuickOrderInputDto());
        return "quickOrder";
    }

    @PostMapping
    public ModelAndView quickOrder( final @ModelAttribute @Validated QuickOrderInputDto quickOrderInputDto,
                                    final BindingResult bindingResult ) {
        List<QuickOrderItemInputDto> itemsToAdd = quickOrderService.getItemsToAdd(quickOrderInputDto, bindingResult);
        Map<String, Long> modelIdMap = phoneDao.findAllByModels(itemsToAdd.stream()
                                                    .map(QuickOrderItemInputDto::getModel)
                                                    .collect(Collectors.toList()))
                                               .stream()
                                               .collect(Collectors.toMap(Phone::getModel, Phone::getId));
        ModelAndView modelAndView = new ModelAndView();
        if (!itemsToAdd.isEmpty()) {
            try {
                for (QuickOrderItemInputDto item: itemsToAdd) {
                    cartService.addPhone(modelIdMap.get(item.getModel()), item.getRequestedQuantity());
                }
                modelAndView.addObject("successMessage",
                            itemsToAdd.size() + " items added to cart successfully");
            } catch (OutOfStockException e) {
                return getOutOfStockModelAndView(bindingResult, quickOrderInputDto, itemsToAdd);
            }
        }
        configureModelAndView(modelAndView, bindingResult, quickOrderInputDto, itemsToAdd);
        return modelAndView;
    }

    private ModelAndView getOutOfStockModelAndView( final BindingResult bindingResult,
                                                    final QuickOrderInputDto quickOrderInputDto,
                                                    final List<QuickOrderItemInputDto> itemsToAdd ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", "Some error has occurred, please, try again");
        configureErrorModelAndView(modelAndView, bindingResult, quickOrderInputDto, itemsToAdd);
        return modelAndView;
    }

    private void configureModelAndView( final ModelAndView modelAndView,
                                        final BindingResult bindingResult,
                                        final QuickOrderInputDto quickOrderInputDto,
                                        final List<QuickOrderItemInputDto> itemsToAdd ) {
        if (bindingResult.hasErrors()) {
            configureErrorModelAndView(modelAndView, bindingResult, quickOrderInputDto, itemsToAdd);
        } else {
            modelAndView.setViewName("redirect:/quickOrder");
        }
    }

    private void configureErrorModelAndView( final ModelAndView modelAndView,
                                             final BindingResult bindingResult,
                                             final QuickOrderInputDto quickOrderInputDto,
                                             final List<QuickOrderItemInputDto> itemsToAdd ) {
        quickOrderService.replaceAddedToCartItems(quickOrderInputDto, itemsToAdd);
        modelAndView.addObject("org.springframework.validation.BindingResult.quickOrderInputDto",
                quickOrderService.getCleanedUpBindingResult(quickOrderInputDto, bindingResult));
        modelAndView.addObject("quickOrderInputDto", quickOrderInputDto);
        modelAndView.setViewName("quickOrder");
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
    }
}