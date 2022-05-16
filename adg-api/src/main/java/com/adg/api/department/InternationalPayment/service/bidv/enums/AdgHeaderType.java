package com.adg.api.department.InternationalPayment.service.bidv.enums;

import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.ParserUtils;
import org.apache.poi.ss.usermodel.CellType;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.10 22:48
 */
public enum AdgHeaderType {

    STRING(
            String.class,
            CellType.STRING,
            Objects::nonNull,
            MapUtils::getString
    ),
    DOUBLE(
            Double.class,
            CellType.NUMERIC,
            raw -> {
                if (raw == null) return false;
                if (raw.equals("0")) return true;
                if (raw.equals("0.0")) return true;
                return ParserUtils.toDouble(raw, 0) != 0;
            },
            MapUtils::getDouble
    ),
    INTEGER(
            Integer.class,
            CellType.NUMERIC,
            raw -> {
                if (raw == null) return false;
                if (raw.equals("0")) return true;
                return ParserUtils.toInt(raw, 0) != 0;
            },
            MapUtils::getInt
    ),
    BOOLEAN(
            Boolean.class,
            CellType.BOOLEAN,
            raw -> {
                if (raw == null) return false;
                return raw.equalsIgnoreCase("false") || raw.equalsIgnoreCase("true");
            },
            MapUtils::getBoolean
    ),
    DATE(
            ZonedDateTime.class,
            CellType.NUMERIC,
            raw -> {
                if (raw == null) return false;
                return DateTimeUtils.verifyDateTimeFormat(raw, DateTimeUtils.FMT_01);
            },
            MapUtils::getZonedDateTime
    );

    public final Class javaType;
    public final CellType excelType;
    public final Function<String, Boolean> verifyMethod;
    public final BiFunction<Map<String, Object>, String, Object> getMap;

    <T> AdgHeaderType(Class<T> javaType, CellType excelType, Function<String, Boolean> verifyMethod, BiFunction<Map<String, Object>, String, T> getMap) {
        this.javaType = javaType;
        this.excelType = excelType;
        this.verifyMethod = verifyMethod;
        this.getMap = (BiFunction<Map<String, Object>, String, Object>) getMap;
    }

}
