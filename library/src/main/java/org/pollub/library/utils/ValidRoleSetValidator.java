package org.pollub.library.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.pollub.library.user.model.Role;

import java.util.Set;

public class ValidRoleSetValidator implements ConstraintValidator<ValidRoleSet, Set<String>> {
    private String customMessage;
    private static final String NULL_ROLE_MESSAGE = "Role cannot be null.";
    private static final String NULL_ROLE_SET_MESSAGE = "Roles set cannot be null";

    @Override
    public void initialize(ValidRoleSet constraintAnnotation) {
        this.customMessage = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Set<String> roles, ConstraintValidatorContext context) {
        if (roles == null) {
            addViolation(context, NULL_ROLE_SET_MESSAGE);
            return false;
        }

        return areRolesValid(roles, context);
    }

    private boolean areRolesValid(Set<String> roles, ConstraintValidatorContext context) {
        boolean allRolesValid = true;

        for (String roleString : roles) {
            if (!isRoleValid(roleString, context)) {
                allRolesValid = false;
            }
        }

        return allRolesValid;
    }

    private boolean isRoleValid(String roleString, ConstraintValidatorContext context) {
        if (roleString == null) {
            addViolation(context, NULL_ROLE_MESSAGE);
            return false;
        }
        try {
            Role.valueOf(roleString);
            return true;
        } catch (IllegalArgumentException e) {
            addViolation(context, customMessage + roleString);
            return false;
        }
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
