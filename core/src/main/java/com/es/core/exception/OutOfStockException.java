package com.es.core.exception;

import java.util.List;

public class OutOfStockException extends Exception {
    public static final String DEFAULT_TEMPLATE_MESSAGE = "The overall requested stock {0} exceeds the available {1}";

    private final List<OutOfStockItem> items;

    public OutOfStockException( final List<OutOfStockItem> items ) {
        this.items = items;
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