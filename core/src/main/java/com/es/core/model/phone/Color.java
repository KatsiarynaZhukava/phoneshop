package com.es.core.model.phone;

public class Color {
    private Long id;
    private String code;

    public Color() { }

    public Color( final Long id, final String code ) {
        this.id = id;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId( final Long id ) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode( final String code ) {
        this.code = code;
    }

    @Override
    public int hashCode() {
        int result = 17;
        if (id != null) {
            result = 31 * result + id.hashCode();
        }
        if (code != null) {
            result = 31 * result + code.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals( final Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Color))
            return false;
        final Color other = (Color) obj;
        boolean idEquals = (this.id == null && other.id == null)
                || (this.id != null && this.id.equals(other.id));
        boolean codeEquals = (this.code == null && other.code == null)
                || (this.code != null && this.code.equals(other.code));
        return idEquals && codeEquals;
    }
}