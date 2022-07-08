package com.adg.api.department.InternationalPayment.disbursement.office.excel;

import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.ParserUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.19 21:29
 */
public class ExcelUtils {

    public static List<String> parseRow(@Nonnull Row row, int maxCellNum) {
        List<String> rowValue = new ArrayList<>();

        for (int i = 0; i <= maxCellNum; i++) {

            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = parseCell(cell);
            }

            if (row.getRowNum() == 0) {
                rowValue.add(cellValue == null ? "_c" + i : cellValue);
            } else {
                rowValue.add(cellValue == null ? "" : cellValue);
            }
        }

        return rowValue;
    }

    public static List<String> parseSelectedRow(@Nonnull Row row, List<Integer> columnIndices) {
        List<String> rowValue = new ArrayList<>();
        for (Integer columnIndex : columnIndices) {
            Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            rowValue.add(parseCell(cell));
        }
        return rowValue;
    }

    public static String parseCell(@Nonnull Cell cell) {
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING: {
                return parseString(cell);
            }
            case NUMERIC: {
                return parseNumeric(cell);
            }
            case BOOLEAN: {
                return parseBoolean(cell);
            }
            case FORMULA: {
                return parseFormula(cell);
            }
            case _NONE:
            case BLANK:
            case ERROR: {
                return null;
            }
        }
        return null;
    }

    public static String parseNumeric(@Nonnull Cell cell) {
        boolean isNumericCell = cell.getCellType().equals(CellType.NUMERIC);
        boolean isNumericFormula = cell.getCellType().equals(CellType.FORMULA)
                && cell.getCachedFormulaResultType().equals(CellType.NUMERIC);

        if (!isNumericCell && !isNumericFormula) {
            return null;
        }

        try {
            if (DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                return DateTimeUtils.convertZonedDateTimeToFormat(zonedDateTime, ZoneId.systemDefault().getId(), DateTimeUtils.MA_DATE_TIME_FORMATTER);
            } else {
                double cellValue = cell.getNumericCellValue();

                DecimalFormat df = new DecimalFormat("#");
                df.setMaximumFractionDigits(20);
                return df.format(cellValue);
//                return String.format("%.20f", cellValue);
            }
        } catch (Exception exception) {
            return null;
        }
    }

    public static String parseBoolean(@Nonnull Cell cell) {
        boolean isBooleanCell = cell.getCellType().equals(CellType.BOOLEAN);
        boolean isBooleanFormula = cell.getCellType().equals(CellType.FORMULA)
                && cell.getCachedFormulaResultType().equals(CellType.BOOLEAN);

        if (!isBooleanCell && !isBooleanFormula) {
            return null;
        }

        try {
            boolean cellValue = cell.getBooleanCellValue();
            return cellValue ? "true" : "false";
        } catch (Exception exception) {
            return null;
        }

    }

    public static String parseString(@Nonnull Cell cell) {
        boolean isStringCell = cell.getCellType().equals(CellType.STRING);
        boolean isStringFormula = cell.getCellType().equals(CellType.FORMULA)
                && cell.getCachedFormulaResultType().equals(CellType.STRING);

        if (!isStringCell && !isStringFormula) {
            return null;
        }

        try {
            String str = cell.getStringCellValue();
            if (!ParserUtils.isNullOrEmpty(str)) {
                str = str.replace("\n", " ");
                str = str.replace("\t", " ");

                /**
                 * replace because of import json object (array, object)
                 */
//                str = str.replace("\"", " ");

                str = str.replace("\\", " ");
            }
            return str;
        } catch (Exception exception) {
            return null;
        }
    }

    public static String parseFormula(@Nonnull Cell cell) {

        CellType cellType = cell.getCellType();

        if (!cellType.equals(CellType.FORMULA)) {
            return null;
        }

        CellType cellFormulaType = cell.getCachedFormulaResultType();

        switch (cellFormulaType) {
            case BOOLEAN: {
                return parseBoolean(cell);
            }
            case NUMERIC: {
                return parseNumeric(cell);
            }
            case STRING: {
                return parseString(cell);
            }
            default: {
                return null;
            }
        }
    }

    public static void setCell(Cell cell, Object value, CellType cellType) {
        switch (cellType) {
            case STRING: {
                String val = value.toString();
                cell.setCellValue(val);
                return;
            }
            case NUMERIC: {
                double val = ParserUtils.toDouble(value);
                cell.setCellValue(val);
                return;
            }
            case BOOLEAN: {
                boolean val = ParserUtils.toBoolean(value);
                cell.setCellValue(val);
                return;
            }
            case FORMULA: {
                String val = value.toString();
                cell.setCellFormula(val);
                return;
            }
            case _NONE:
            case BLANK:
            case ERROR: {
                cell.setCellValue("");
            }
        }
    }

}
