package com.es.phoneshop.web.validation;

import com.es.core.dto.input.QuickOrderInputDto;
import com.es.core.dto.input.QuickOrderItemInputDto;
import com.es.core.exception.NotFoundException;
import com.es.core.exception.OutOfStockException;
import com.es.core.service.CartService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuickOrderValidator implements Validator {
    @Resource
    private CartService cartService;

    @Override
    public boolean supports(Class<?> aClass) {
        return QuickOrderInputDto.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        QuickOrderInputDto quickOrderInputDto = (QuickOrderInputDto) o;
        List<QuickOrderItemInputDto> items = quickOrderInputDto.getItems().stream()
                .filter(item -> item.getRequestedQuantity() != null)
                .filter(item -> !item.getModel().isEmpty())
                .collect(Collectors.toList());
        for (QuickOrderItemInputDto item: items) {
            try {
                cartService.checkIfStockExceeded(item.getModel(), item.getRequestedQuantity());
            } catch (NotFoundException e) {
                errors.rejectValue("items[" + item.getRowNumber() + "].model",
                          "invalidValue.model", e.getMessage());
            } catch (OutOfStockException e) {
                errors.rejectValue("items[" + item.getRowNumber() + "].requestedQuantity",
                            "stockExceeded.requestedQuantity", e.getMessage());
            }
        }
    }
}