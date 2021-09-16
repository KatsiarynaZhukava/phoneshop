package com.es.core.dto.input;


public class QuickOrderItemInputDto {
    private String model;
    private Long rowNumber;
    private Long requestedQuantity;

    public String getModel() { return model; }

    public void setModel(String model) { this.model = model; }

    public Long getRowNumber() { return rowNumber; }

    public void setRowNumber(Long rowNumber) { this.rowNumber = rowNumber; }

    public Long getRequestedQuantity() { return requestedQuantity; }

    public void setRequestedQuantity(Long requestedQuantity) { this.requestedQuantity = requestedQuantity; }
}