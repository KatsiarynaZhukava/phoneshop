package com.es.core.dto.input;


import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class QuickOrderItemInputDto {
    @NotEmpty
    private String model;
    private Long rowNumber;
    @NotNull
    @Min(value = 1)
    private Long requestedQuantity;

    public String getModel() { return model; }

    public void setModel(String model) { this.model = model; }

    public Long getRowNumber() { return rowNumber; }

    public void setRowNumber(Long rowNumber) { this.rowNumber = rowNumber; }

    public Long getRequestedQuantity() { return requestedQuantity; }

    public void setRequestedQuantity(Long requestedQuantity) { this.requestedQuantity = requestedQuantity; }
}