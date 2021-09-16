package com.es.core.service;

import com.es.core.dto.input.QuickOrderInputDto;
import com.es.core.dto.input.QuickOrderItemInputDto;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;
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
        for (ObjectError error : bindingResult.getAllErrors()) {
            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                Integer errorItemIndex = Integer.parseInt(fieldName.substring(fieldName.indexOf('[') + 1, fieldName.indexOf(']')));

                if (!errorItemsIndexes.contains(errorItemIndex)) {
                    errorItemsIndexes.add(errorItemIndex);
                }
            }
        }
        List<QuickOrderItemInputDto> itemsToAdd = new ArrayList<>();
        List<QuickOrderItemInputDto> allItems = quickOrderInputDto.getItems();
        for (int i = 0; i < allItems.size(); i++) {
            if (!errorItemsIndexes.contains(i)) {
                itemsToAdd.add(allItems.get(i));
            }
        }
        return itemsToAdd.stream().filter(item -> (item.getRequestedQuantity() != null) &&
                                                   !item.getModel().isEmpty())
                                  .collect(Collectors.toList());
    }
}
