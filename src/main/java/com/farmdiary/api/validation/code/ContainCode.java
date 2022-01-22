package com.farmdiary.api.validation.code;

import com.farmdiary.api.entity.BaseEnum;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = ContainCodeValidator.class)
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ContainCode {

    String message() default "잘못된 코드입니다.";
    Class<?>[] groups() default {};
    Class<? extends BaseEnum> target();
    Class<? extends Payload>[] payload() default {};
}
