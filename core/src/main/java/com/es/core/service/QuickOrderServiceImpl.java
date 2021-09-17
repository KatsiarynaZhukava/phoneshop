package com.es.core.service;

import com.es.core.dto.input.QuickOrderInputDto;
import com.es.core.dto.input.QuickOrderItemInputDto;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class QuickOrderServiceImpl implements QuickOrderService {

    @Override
    public void replaceAddedToCartItems( final QuickOrderInputDto quickOrderInputDto,
                                         final List<QuickOrderItemInputDto> itemsToAdd ) {
        List<QuickOrderItemInputDto> allItems = quickOrderInputDto.getItems();
        for (int i = 0; i < allItems.size(); i++) {
            if (itemsToAdd.contains(allItems.get(i))) {
                allItems.set(i, new QuickOrderItemInputDto());
            }
        }
    }

    @Override
    public List<QuickOrderItemInputDto> getItemsToAdd( final QuickOrderInputDto quickOrderInputDto,
                                                       final BindingResult bindingResult ) {
        List<Integer> errorItemsIndexes = new ArrayList<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            String fieldName = error.getField();
            Integer errorItemIndex = Integer.parseInt(fieldName.substring(fieldName.indexOf('[') + 1,
                                                      fieldName.indexOf(']')));
            if (!errorItemsIndexes.contains(errorItemIndex)) {
                errorItemsIndexes.add(errorItemIndex);
            }
        }
        List<QuickOrderItemInputDto> itemsToAdd = new ArrayList<>();
        List<QuickOrderItemInputDto> allItems = quickOrderInputDto.getItems();
        for (int i = 0; i < allItems.size(); i++) {
            if (!errorItemsIndexes.contains(i)) {
                itemsToAdd.add(allItems.get(i));
            }
        }
        return itemsToAdd.stream().filter(item -> item.getRequestedQuantity() != null)
                                  .filter(item -> !item.getModel().isEmpty())
                                  .collect(Collectors.toList());
    }

    @Override
    public BindingResult getCleanedUpBindingResult( final QuickOrderInputDto quickOrderInputDto,
                                                    final BindingResult bindingResult ) {
        List<String> emptyElementNames = getElementsWithBothFieldsEmpty(bindingResult);
        BindingResult newBindingResult = new BeanPropertyBindingResult(quickOrderInputDto, "quickOrderInputDto");

        for (FieldError error: bindingResult.getFieldErrors()) {
            if (!emptyElementNames.contains(error.getField().substring(0, error.getField().indexOf('.')))) {
                newBindingResult.addError(error);
            }
        }
        return newBindingResult;
    }

    private List<String> getElementsWithBothFieldsEmpty( final BindingResult bindingResult ) {
        List<String> nullQuantityElementNames = new ArrayList<>();
        List<String> emptyModelElementNames = new ArrayList<>();
        for (FieldError error: bindingResult.getFieldErrors()) {
            String fieldName = error.getField();
            if (error.getRejectedValue() == null) {
                nullQuantityElementNames.add(fieldName.substring(0, fieldName.indexOf('.')));
            } else if (Objects.equals(error.getRejectedValue(), "")) {
                emptyModelElementNames.add(fieldName.substring(0, fieldName.indexOf('.')));
            }
        }
        nullQuantityElementNames.retainAll(emptyModelElementNames);
        return nullQuantityElementNames;
    }
}