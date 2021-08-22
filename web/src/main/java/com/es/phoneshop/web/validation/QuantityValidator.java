package com.es.phoneshop.web.validation;

import com.es.core.dao.StockDao;
import com.es.core.model.phone.Stock;
import com.es.phoneshop.web.dto.input.CartInputDto;
import com.es.phoneshop.web.dto.input.CartItemInputDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

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

        for (int i = 0; i < items.size(); i++) {
            CartItemInputDto item = items.get(i);
            Long stockRequested = item.getRequestedQuantity();
            Long stockAvailable = stockDao.get(item.getPhoneId()).map(Stock::getStock).orElse(0L);
            if (stockRequested > stockAvailable) {
                errors.rejectValue("items[" + i + "].requestedQuantity", "Stock exceeded",
                                    MessageFormat.format("The overall requested stock {0} exceeds the available {1}",
                                                         stockRequested, stockAvailable ));
            }
        }
    }
}