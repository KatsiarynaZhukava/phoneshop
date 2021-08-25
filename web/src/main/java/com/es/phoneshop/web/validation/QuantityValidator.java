package com.es.phoneshop.web.validation;

import com.es.core.dao.StockDao;
import com.es.core.dto.input.CartInputDto;
import com.es.core.dto.input.CartItemInputDto;
import com.es.core.model.phone.Stock;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class QuantityValidator implements Validator {
    @Resource
    private StockDao stockDao;

    @Override
    public boolean supports(Class<?> aClass) {
        return CartInputDto.class.equals(aClass);
    }

    @Override
    public void validate( final Object o, final Errors errors ) {
        CartInputDto cartInputDto = (CartInputDto) o;
        List<CartItemInputDto> items = new ArrayList<>(cartInputDto.getItems());

        Map<Long, Stock> stocks = stockDao.findAll( new ArrayList<>(items.stream().map(CartItemInputDto::getPhoneId).collect(Collectors.toList())))
                                          .stream()
                                          .collect( Collectors.toMap( stock -> stock.getPhone().getId(),
                                                                      Function.identity()) );
        for (int i = 0; i < items.size(); i++) {
            CartItemInputDto item = items.get(i);
            Stock itemStock = stocks.get(item.getPhoneId());
            Long stockAvailable = itemStock == null ? 0 : itemStock.getStock();
            Long stockRequested = item.getRequestedQuantity();
            if (stockRequested != null) {
                if (stockRequested > stockAvailable) {
                    errors.rejectValue("items[" + i + "].requestedQuantity", "Stock exceeded",
                            MessageFormat.format("The overall requested stock {0} exceeds the available {1}",
                                    stockRequested, stockAvailable ));
                }
            }
        }
    }
}