package com.farmdiary.api.validation.string;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotWhiteSpaceValidator implements ConstraintValidator<NotWhiteSpace, String> {

    @Override
    public void initialize(NotWhiteSpace constraintAnnotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return (null == value) || (!value.isBlank());
    }
}
