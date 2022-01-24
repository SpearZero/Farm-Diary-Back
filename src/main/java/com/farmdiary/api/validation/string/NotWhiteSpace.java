package com.farmdiary.api.validation.string;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = NotWhiteSpaceValidator.class)
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface NotWhiteSpace {

    String message() default "입력값이 공백입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
