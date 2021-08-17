package com.es.phoneshop.web.enums;

public enum SortOrder {
    ASC ("asc"),
    DESC ("desc");

    private final String name;

    private SortOrder(String s) { name = s; }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() { return this.name; }
}
