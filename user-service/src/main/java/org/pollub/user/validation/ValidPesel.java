package org.pollub.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation for PESEL (Polish ID number).
 * PESEL must be 11 digits and pass checksum validation.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PeselValidator.class)
@Documented
public @interface ValidPesel {
    String message() default "Invalid PESEL number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
