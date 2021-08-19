package com.es.phoneshop.web.dto.input;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CartInputDto {
    @NotNull
    @Valid
    private List<CartItemInputDto> items;

    public CartInputDto() {}

    public CartInputDto(List<CartItemInputDto> items) {
        this.items = items;
    }

    public List<CartItemInputDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemInputDto> items) {
        this.items = items;
    }
}