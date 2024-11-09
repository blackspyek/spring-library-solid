package org.pollub.library.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidRoleSetValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRoleSet {
    String message() default "Invalid role value ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
