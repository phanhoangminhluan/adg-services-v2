package com.adg.api.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.ParserUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 02:13
 */
public class JacksonDeserializer {

    public static class StringToDouble extends JsonDeserializer<Double> {
        @Override
        public Double deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            String text = jsonParser.getText();
            Double db = ParserUtils.toDouble(text, 0);
            return db;
        }
    }

    public static class StringToLocalDate extends JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            String text = jsonParser.getText();
            if (Objects.isNull(text)) return null;

            if (DateTimeUtils.verifyDateTimeFormat(text,DateTimeUtils.FMT_03)) {
                ZonedDateTime zonedDateTime = DateTimeUtils.convertStringToZonedDateTime(text, DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_03), "UTC", "UTC");
                if (Objects.isNull(zonedDateTime)) return null;
                return zonedDateTime.toLocalDate();
            }

            if (DateTimeUtils.verifyDateTimeFormat(text,DateTimeUtils.FMT_01)) {
                ZonedDateTime zonedDateTime = DateTimeUtils.convertStringToZonedDateTime(text, DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_01), "UTC", "UTC");
                if (Objects.isNull(zonedDateTime)) return null;
                return zonedDateTime.toLocalDate();
            }

            return null;
        }
    }
}
