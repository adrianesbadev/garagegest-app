package com.adrian.taller_app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MatriculaEspanolaValidator implements ConstraintValidator<MatriculaEspanola, String> {

    @Override
    public void initialize(MatriculaEspanola constraintAnnotation) {
        // No hay inicialización necesaria
    }

    @Override
    public boolean isValid(String matricula, ConstraintValidatorContext context) {
        if (matricula == null || matricula.trim().isEmpty()) {
            return false;
        }

        // Normalizar: quitar espacios, guiones y convertir a mayúsculas
        String normalized = matricula.trim().toUpperCase()
                .replaceAll("\\s+", "")
                .replaceAll("-", "");

        // Formato nuevo (desde septiembre 2000): 4 dígitos + 3 letras (sin vocales ni Q)
        // Ejemplo: 1234ABC, 5678XYZ
        if (normalized.matches("^[0-9]{4}[BCDFGHJKLMNPRSTVWXYZ]{3}$")) {
            return true;
        }

        // Formato antiguo (1971-2000): 1-2 letras (provincia) + 4 números + 1-2 letras
        // Ejemplos: M1234AB, SE1234A, B1234BC
        if (normalized.matches("^[A-Z]{1,2}[0-9]{4}[A-Z]{1,2}$")) {
            return true;
        }

        return false;
    }
}
