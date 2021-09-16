package com.es.phoneshop.web.validation;

import com.es.core.dao.PhoneDao;
import com.es.core.dao.StockDao;
import com.es.core.dto.input.QuickOrderInputDto;
import com.es.core.dto.input.QuickOrderItemInputDto;
import com.es.core.exception.MultipleResultValuesException;
import com.es.core.exception.NotFoundException;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
import com.es.core.service.CartService;
import com.es.core.util.PhoneShopMessages;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class QuickOrderValidator implements Validator {
    @Resource
    private PhoneDao phoneDao;
    @Resource
    private StockDao stockDao;
    @Resource
    private CartService cartService;

    @Override
    public boolean supports(Class<?> aClass) {
        return QuickOrderInputDto.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        QuickOrderInputDto quickOrderInputDto = (QuickOrderInputDto) o;
        List<QuickOrderItemInputDto> items = quickOrderInputDto.getItems()
                                                               .stream()
                                                               .filter(item -> (item.getRequestedQuantity() != null) ||
                                                                                !item.getModel().isEmpty())
                                                               .collect(Collectors.toList());
        for (int i = 0; i < items.size(); i++) {
            QuickOrderItemInputDto item = items.get(i);
            Long requestedQuantity = item.getRequestedQuantity();
            if (requestedQuantity == null) {
                errors.rejectValue("items[" + item.getRowNumber() + "].requestedQuantity",
                          "Empty field", "The quantity field should not be empty");
            } else if (requestedQuantity < 1) {
                errors.rejectValue("items[" + item.getRowNumber() + "].requestedQuantity",
                              "Invalid quantity value", "The quantity field should be > 0");
            } else {
                validateModelAndStock(errors, item, requestedQuantity);
            }
        }
    }

    private void validateModelAndStock( final Errors errors,
                                        final QuickOrderItemInputDto item,
                                        final Long requestedQuantity ) {
        String model = item.getModel();
        if (model.isEmpty()) {
            errors.rejectValue("items[" + item.getRowNumber() + "].model",
                    "Empty field", "The model field should not be empty");
        } else {
            try {
                Phone phone = phoneDao.getByModel(model)
                                      .orElseThrow(NotFoundException.supplier(PhoneShopMessages.PHONE_NOT_FOUND_BY_MODEL_MESSAGE, model));
                Stock stock = stockDao.get(phone.getId())
                                      .orElseThrow(NotFoundException.supplier(PhoneShopMessages.STOCK_NOT_FOUND_BY_PHONE_ID, phone.getId()));
                Map<Long, Long> cartItems = cartService.getCart().getItems();

                Long overallRequestedQuantity = requestedQuantity;
                if (cartItems.containsKey(phone.getId())) overallRequestedQuantity += cartItems.get(phone.getId());

                if (overallRequestedQuantity > stock.getStock() - stock.getReserved()) {
                    errors.rejectValue("items[" + item.getRowNumber() + "].requestedQuantity", "Stock exceeded",
                                       MessageFormat.format("The overall requested stock {0} exceeds the available {1}",
                                                    overallRequestedQuantity, stock.getStock() - stock.getReserved()));
                }
            } catch (MultipleResultValuesException | NotFoundException e) {
                errors.rejectValue("items[" + item.getRowNumber() + "].model", "Invalid result", e.getMessage());
            }
        }
    }
}