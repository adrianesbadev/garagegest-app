package com.adrian.taller_app.domain.converter;

import com.adrian.taller_app.domain.RolUsuario;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RolUsuarioConverter implements AttributeConverter<RolUsuario, String> {

    @Override
    public String convertToDatabaseColumn(RolUsuario attribute) {
        return attribute != null ? attribute.getValor() : null;
    }

    @Override
    public RolUsuario convertToEntityAttribute(String dbData) {
        return RolUsuario.fromValor(dbData);
    }
}
