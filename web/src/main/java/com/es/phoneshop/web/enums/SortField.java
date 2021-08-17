package com.es.phoneshop.web.enums;

public enum SortField {
    BRAND ("brand"),
    MODEL ("model"),
    DISPLAY_SIZE ("displaySizeInches"),
    PRICE ("price");

    private final String name;

    private SortField(String s) { name = s; }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() { return this.name; }
}