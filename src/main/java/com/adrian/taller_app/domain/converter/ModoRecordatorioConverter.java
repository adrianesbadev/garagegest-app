package com.adrian.taller_app.domain.converter;

import com.adrian.taller_app.domain.ModoRecordatorio;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ModoRecordatorioConverter implements AttributeConverter<ModoRecordatorio, String> {

    @Override
    public String convertToDatabaseColumn(ModoRecordatorio attribute) {
        return attribute != null ? attribute.getValor() : null;
    }

    @Override
    public ModoRecordatorio convertToEntityAttribute(String dbData) {
        return ModoRecordatorio.fromValor(dbData);
    }
}
