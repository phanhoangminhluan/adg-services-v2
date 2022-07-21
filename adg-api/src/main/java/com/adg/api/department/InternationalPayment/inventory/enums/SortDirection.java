package com.adg.api.department.InternationalPayment.inventory.enums;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.21 23:26
 */
public enum SortDirection {
    ASC,
    DESC;

    public static class SortDirectionJsonDeserializer extends JsonDeserializer<SortDirection> {

        @Override
        public SortDirection deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            String text = jsonParser.getText();
            return SortDirection.valueOf(text.toUpperCase());
        }
    }
}
