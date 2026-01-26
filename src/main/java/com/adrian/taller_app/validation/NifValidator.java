package com.adrian.taller_app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NifValidator implements ConstraintValidator<Nif, String> {

    private boolean required;

    @Override
    public void initialize(Nif constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String nif, ConstraintValidatorContext context) {
        // Si no es requerido y está vacío, es válido
        if (!required && (nif == null || nif.trim().isEmpty())) {
            return true;
        }

        // Si es requerido y está vacío, no es válido
        if (required && (nif == null || nif.trim().isEmpty())) {
            return false;
        }

        // Normalizar: quitar espacios y convertir a mayúsculas
        String normalized = nif.trim().toUpperCase().replaceAll("\\s+", "");

        // Validar formato básico
        if (normalized.length() < 8 || normalized.length() > 9) {
            return false;
        }

        // Validar NIF (8 dígitos + 1 letra)
        if (normalized.matches("^[0-9]{8}[A-Z]$")) {
            return validarNif(normalized);
        }

        // Validar CIF (letra + 7 dígitos + letra o número)
        if (normalized.matches("^[A-Z][0-9]{7}[0-9A-Z]$")) {
            return validarCif(normalized);
        }

        // Validar NIE (X/Y/Z + 7 dígitos + letra)
        if (normalized.matches("^[XYZ][0-9]{7}[A-Z]$")) {
            return validarNie(normalized);
        }

        return false;
    }

    private boolean validarNif(String nif) {
        try {
            String numero = nif.substring(0, 8);
            String letra = nif.substring(8, 9);
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
            int resto = Integer.parseInt(numero) % 23;
            return letras.charAt(resto) == letra.charAt(0);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validarCif(String cif) {
        try {
            String letraInicial = cif.substring(0, 1);
            String numero = cif.substring(1, 8);
            String digitoControl = cif.substring(8, 9);

            // Calcular dígito de control
            int suma = 0;
            for (int i = 0; i < numero.length(); i++) {
                int digito = Character.getNumericValue(numero.charAt(i));
                if (i % 2 == 0) {
                    digito *= 2;
                    if (digito > 9) {
                        digito = (digito / 10) + (digito % 10);
                    }
                }
                suma += digito;
            }

            int resto = suma % 10;
            int digitoCalculado = (10 - resto) % 10;

            // Verificar según el tipo de CIF
            if (letraInicial.matches("[ABEH]")) {
                // Debe ser una letra
                String letras = "JABCDEFGHI";
                return letras.charAt(digitoCalculado) == digitoControl.charAt(0);
            } else if (letraInicial.matches("[NPQRSW]")) {
                // Debe ser un número
                return String.valueOf(digitoCalculado).equals(digitoControl);
            } else {
                // Puede ser letra o número
                String letras = "JABCDEFGHI";
                return String.valueOf(digitoCalculado).equals(digitoControl) ||
                       letras.charAt(digitoCalculado) == digitoControl.charAt(0);
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validarNie(String nie) {
        try {
            String letraInicial = nie.substring(0, 1);
            String numero = nie.substring(1, 8);
            String letra = nie.substring(8, 9);

            // Reemplazar letra inicial por número
            String numeroNif = letraInicial.replace("X", "0")
                                          .replace("Y", "1")
                                          .replace("Z", "2") + numero;

            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
            int resto = Integer.parseInt(numeroNif) % 23;
            return letras.charAt(resto) == letra.charAt(0);
        } catch (Exception e) {
            return false;
        }
    }
}
