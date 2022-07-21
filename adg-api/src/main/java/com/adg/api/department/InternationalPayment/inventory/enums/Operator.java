package com.adg.api.department.InternationalPayment.inventory.enums;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.22 00:22
 */
public enum Operator {

    EXIST,
    NOT_EXIST,
    EQUAL,
    NOT_EQUAL,
    CONTAIN,
    NOT_CONTAIN,
    GREATER_THAN,
    LESS_THAN,
    BETWEEN,
    DAYS_AGO,
    BEFORE,
    AFTER;

    public static class OperatorJsonDeserializer extends JsonDeserializer<Operator> {

        @Override
        public Operator deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            String text = jsonParser.getText();
            return Operator.valueOf(text.toUpperCase());
        }
    }

}
