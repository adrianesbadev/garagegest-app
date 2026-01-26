package com.adrian.taller_app.domain.converter;

import com.adrian.taller_app.domain.EstadoOrdenTrabajo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EstadoOrdenTrabajoConverter implements AttributeConverter<EstadoOrdenTrabajo, String> {

    @Override
    public String convertToDatabaseColumn(EstadoOrdenTrabajo attribute) {
        return attribute != null ? attribute.getValor() : null;
    }

    @Override
    public EstadoOrdenTrabajo convertToEntityAttribute(String dbData) {
        return EstadoOrdenTrabajo.fromValor(dbData);
    }
}
