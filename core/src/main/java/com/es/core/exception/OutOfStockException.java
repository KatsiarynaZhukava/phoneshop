package com.es.core.exception;

import com.es.core.util.PhoneShopMessages;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class OutOfStockException extends Exception {
    private final List<OutOfStockItem> items;

    public OutOfStockException( final List<OutOfStockItem> items ) {
        super("The overall requested stock exceeds the available one");
        this.items = items;
    }

    public OutOfStockException( final Long phoneId, final Long stockRequested, final Long stockAvailable ) {
        super( MessageFormat.format(PhoneShopMessages.OUT_OF_STOCK_MESSAGE, stockRequested, stockAvailable ));
        items = new ArrayList<>();
        items.add(new OutOfStockItem(phoneId, stockRequested, stockAvailable));
    }

    public List<OutOfStockItem> getItems() { return items; }
}