package com.es.phoneshop.web.dto.output;

import com.es.core.model.phone.Phone;

import java.util.Map;

public class DetailedCartOutputDto {
    Map<Phone, Long> items;

    public DetailedCartOutputDto() {}

    public DetailedCartOutputDto(final Map<Phone, Long> items ) {
        this.items = items;
    }

    public Map<Phone, Long> getItems() {
        return items;
    }

    public void setItems(Map<Phone, Long> items) {
        this.items = items;
    }
}