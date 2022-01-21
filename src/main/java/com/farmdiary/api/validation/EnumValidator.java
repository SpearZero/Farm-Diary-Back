package com.farmdiary.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<ContainCode, String> {

    private List<String> codes;

    @Override
    public void initialize(ContainCode constraintAnnotation) {
        codes = Arrays.stream(constraintAnnotation.target().getEnumConstants())
                .map(constant -> constant.getCode())
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return codes.contains(value);
    }
}
