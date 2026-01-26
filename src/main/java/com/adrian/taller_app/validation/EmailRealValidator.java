package com.adrian.taller_app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailRealValidator implements ConstraintValidator<EmailReal, String> {

    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    
    private boolean required;

    @Override
    public void initialize(EmailReal constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        // Si no es requerido y está vacío, es válido
        if (!required && (email == null || email.trim().isEmpty())) {
            return true;
        }

        // Si es requerido y está vacío, no es válido
        if (required && (email == null || email.trim().isEmpty())) {
            return false;
        }

        // Validar formato de email
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Validar longitud máxima
        if (email.length() > 100) {
            return false;
        }

        // Validar formato con regex
        return pattern.matcher(email.trim()).matches();
    }
}
