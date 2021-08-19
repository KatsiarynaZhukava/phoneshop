package com.es.phoneshop.web.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class NotFoundException extends RuntimeException {
    public NotFoundException(final String message ) {
        super( message );
    }
    
    public NotFoundException(final String templateMessage, final Object... params ) {
        super( MessageFormat.format( templateMessage, params ) );
    }
    
    public static Supplier<NotFoundException> supplier( final String templateMessage,
                                                        final Object ... params ) {
        return () -> new NotFoundException( templateMessage, params );
    }
}