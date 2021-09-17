package com.es.core.service;

import com.es.core.dto.input.QuickOrderInputDto;
import com.es.core.dto.input.QuickOrderItemInputDto;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface QuickOrderService {
    void replaceAddedToCartItems(QuickOrderInputDto quickOrderInputDto, List<QuickOrderItemInputDto> itemsToAdd);
    List<QuickOrderItemInputDto> getItemsToAdd(QuickOrderInputDto quickOrderInputDto,BindingResult bindingResult);
    BindingResult getCleanedUpBindingResult(QuickOrderInputDto quickOrderInputDto, BindingResult bindingResult);
}
