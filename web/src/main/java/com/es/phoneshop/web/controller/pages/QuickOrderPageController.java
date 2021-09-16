package com.es.phoneshop.web.controller.pages;

import com.es.core.dao.PhoneDao;
import com.es.core.dto.input.QuickOrderInputDto;
import com.es.core.dto.input.QuickOrderItemInputDto;
import com.es.core.exception.OutOfStockException;
import com.es.core.exception.OutOfStockItem;
import com.es.core.model.phone.Phone;
import com.es.core.service.CartService;
import com.es.core.service.QuickOrderService;
import com.es.core.util.PhoneShopMessages;
import com.es.phoneshop.web.validation.QuickOrderValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
        if (!model.containsAttribute("quickOrderInputDto")) {
            model.addAttribute("quickOrderInputDto", new QuickOrderInputDto());
        }
        model.addAttribute("cart", cartService.getCartTotalsOutputDto());
        return "quickOrder";
    }

    @PostMapping
    public String quickOrder( final @ModelAttribute @Validated QuickOrderInputDto quickOrderInputDto,
                              final BindingResult bindingResult,
                              final RedirectAttributes redirectAttributes ) {
        List<QuickOrderItemInputDto> itemsToAdd;
        if (bindingResult.hasErrors()) {
            itemsToAdd = quickOrderService.getItemsToAdd(quickOrderInputDto, bindingResult);
        } else {
            itemsToAdd = quickOrderInputDto.getItems()
                                           .stream()
                                           .filter(item -> (item.getRequestedQuantity() != null) &&
                                                            !item.getModel().isEmpty())
                                           .collect(Collectors.toList());
        }
        Map<String, Long> phoneIdsModelsMap = phoneDao.findAllByModels(itemsToAdd.stream()
                                                                                 .map(QuickOrderItemInputDto::getModel)
                                                                                 .collect(Collectors.toList()))
                                                      .stream()
                                                      .collect( Collectors.toMap(Phone::getModel, Phone::getId));
        Map<Long, Long> itemQuantities = itemsToAdd.stream()
                                                   .collect( Collectors.toMap( item -> phoneIdsModelsMap.get(item.getModel()),
                                                                               QuickOrderItemInputDto::getRequestedQuantity));
        if (!itemsToAdd.isEmpty()) {
            try {
                cartService.update(itemQuantities);
                redirectAttributes.addFlashAttribute("successMessage", itemsToAdd.size() + " items added to cart successfully");
            } catch (OutOfStockException e) {
                Map<Long, OutOfStockItem> outOfStockItems = e.getItems().stream()
                                                                        .collect(Collectors.toMap(OutOfStockItem::getPhoneId,
                                                                                 Function.identity()));
                for (int i = 0; i < itemsToAdd.size(); i++) {
                    QuickOrderItemInputDto item = itemsToAdd.get(i);
                    Long phoneId = phoneIdsModelsMap.get(item.getModel());
                    if (outOfStockItems.containsKey(phoneId)) {
                        bindingResult.rejectValue("items[" + item.getRowNumber() + "].requestedQuantity", "Stock exceeded",
                                                  MessageFormat.format(PhoneShopMessages.OUT_OF_STOCK_MESSAGE,
                                                                       outOfStockItems.get(phoneId).getStockRequested(),
                                                                       outOfStockItems.get(phoneId).getStockAvailable()));
                    }
                }
            }
        }
        if (bindingResult.hasErrors()) {
            quickOrderService.replaceAddedToCartItems(quickOrderInputDto, itemsToAdd);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.quickOrderInputDto", bindingResult);
            redirectAttributes.addFlashAttribute("quickOrderInputDto", quickOrderInputDto);
        }
        return "redirect:/quickOrder";
    }
}