package com.adrian.taller_app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefonoEspanolValidator implements ConstraintValidator<TelefonoEspanol, String> {

    private boolean required;

    @Override
    public void initialize(TelefonoEspanol constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String telefono, ConstraintValidatorContext context) {
        // Si no es requerido y está vacío, es válido
        if (!required && (telefono == null || telefono.trim().isEmpty())) {
            return true;
        }

        // Si es requerido y está vacío, no es válido
        if (required && (telefono == null || telefono.trim().isEmpty())) {
            return false;
        }

        // Normalizar: quitar espacios, guiones, paréntesis y el prefijo +34
        String normalized = telefono.trim()
                .replaceAll("\\s+", "")
                .replaceAll("-", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll("\\+34", "")
                .replaceAll("^0034", "");

        // Debe tener exactamente 9 dígitos
        if (!normalized.matches("^[0-9]{9}$")) {
            return false;
        }

        // El primer dígito debe ser 6, 7, 8 o 9 (móviles) o 9 (fijos)
        char primerDigito = normalized.charAt(0);
        return primerDigito == '6' || primerDigito == '7' || 
               primerDigito == '8' || primerDigito == '9';
    }
}
