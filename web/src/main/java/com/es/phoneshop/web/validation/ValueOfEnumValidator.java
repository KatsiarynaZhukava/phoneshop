package com.es.phoneshop.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, CharSequence> {
    private List<String> acceptedValues;

    @Override
    public void initialize( final ValueOfEnum annotation ) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                               .map(Enum::toString)
                               .collect(Collectors.toList());
    }

    @Override
    public boolean isValid( final CharSequence value, final ConstraintValidatorContext context ) {
        if (value == null) return true;
        return acceptedValues.contains(value.toString()) || value.toString().isEmpty();
    }
}
