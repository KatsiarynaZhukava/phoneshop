package com.es.core.exception;

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
        super( MessageFormat.format("The overall requested stock {0} exceeds the available {1}",
                                    stockRequested, stockAvailable ));
        items = new ArrayList<>();
        items.add(new OutOfStockItem(phoneId, stockRequested, stockAvailable));
    }

    public List<OutOfStockItem> getItems() { return items; }

    public static class OutOfStockItem {
        private final Long phoneId;
        private final Long stockRequested;
        private final Long stockAvailable;

        public OutOfStockItem( final Long phoneId,
                               final Long stockRequested,
                               final Long stockAvailable ) {
            this.phoneId = phoneId;
            this.stockRequested = stockRequested;
            this.stockAvailable = stockAvailable;
        }

        public Long getPhoneId() {
            return phoneId;
        }

        public Long getStockRequested() {
            return stockRequested;
        }

        public Long getStockAvailable() {
            return stockAvailable;
        }
    }
}