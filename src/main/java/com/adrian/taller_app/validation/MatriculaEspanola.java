package com.adrian.taller_app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = MatriculaEspanolaValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MatriculaEspanola {
    String message() default "La matrícula no es válida. Formatos: 1234ABC (nuevo desde 2000) o M1234AB (antiguo 1971-2000)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
