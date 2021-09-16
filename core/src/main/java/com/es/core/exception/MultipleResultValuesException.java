package com.es.core.exception;

import java.text.MessageFormat;

public class MultipleResultValuesException extends Exception {
    public MultipleResultValuesException(final String message ) {
        super( message );
    }

    public MultipleResultValuesException(final String templateMessage, final Object... params ) {
        super( MessageFormat.format( templateMessage, params ) );
    }
}