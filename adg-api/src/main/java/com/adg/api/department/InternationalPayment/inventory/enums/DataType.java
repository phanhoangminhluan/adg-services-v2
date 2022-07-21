package com.adg.api.department.InternationalPayment.inventory.enums;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.22 00:21
 */
public enum DataType {
    STRING,
    NUMBER,
    DATETIME;

    public static class DataTypeDeserializer extends JsonDeserializer<DataType> {
        @Override
        public DataType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            String text = jsonParser.getText();
            return DataType.valueOf(text.toUpperCase());
        }
    }
}
