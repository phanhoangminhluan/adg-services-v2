package com.adg.api.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.merlin.asset.core.utils.DateTimeUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 02:23
 */
public class JacksonSerializer {

    public static class LocalDateToString extends JsonSerializer<LocalDate> {
        @Override
        public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(DateTimeUtils.convertZonedDateTimeToFormat(localDate.atStartOfDay().atZone(ZoneId.of("UTC")), "UTC", DateTimeUtils.FMT_03));
        }
    }

    public static class ZonedDateToString extends JsonSerializer<ZonedDateTime> {
        @Override
        public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(DateTimeUtils.convertZonedDateTimeToFormat(zonedDateTime, "UTC", DateTimeUtils.FMT_03));
        }
    }

}
