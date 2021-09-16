package com.es.core.dto.input;

import java.util.ArrayList;
import java.util.List;

public class QuickOrderInputDto {
    private static final Long MAX_CAPACITY = 10L;
    private List<QuickOrderItemInputDto> items;

    public QuickOrderInputDto() {
        items = new ArrayList<>();
        for (int i = 0; i < MAX_CAPACITY; i++) {
            items.add(new QuickOrderItemInputDto());
        }
    }

    public List<QuickOrderItemInputDto> getItems() { return items; }

    public void setItems(List<QuickOrderItemInputDto> items) { this.items = items; }
}
