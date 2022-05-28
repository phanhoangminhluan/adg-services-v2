package com.adg.api.department.InternationalPayment.handler.office.excel;

import com.google.gson.internal.LinkedTreeMap;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.ParserUtils;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.19 21:35
 */
@Data
public class ExcelReader {

    private String filePath;
    private Workbook workbook;
    private FileInputStream fileInputStream;
    private Sheet sheet;

    public final static String TO_KHAI_HAI_QUAN = "/Users/luan.phm/engineering/Projects/ADongGroup/adg-services-v2/adg-api/src/main/resources/viettin/template-to-khai-hai-quan.json";

    @SneakyThrows
    public ExcelReader(String filePath) {
        this.filePath = filePath;
        this.fileInputStream = new FileInputStream(filePath);

        String fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1);
        if (fileExtension.equals("xls")) {
            this.workbook = new HSSFWorkbook(fileInputStream);
        } else if (fileExtension.equals("xlsx")) {
            this.workbook = new XSSFWorkbook(fileInputStream);
        } else {
            throw new IllegalArgumentException("File extension must be xls or xlsx. Current file extension: " + fileExtension);
        }
        this.sheet = this.workbook.getSheetAt(0);
    }

    public String getCellValueAsString(String cellAddress) {
        Cell cell = this.getCell(cellAddress);
        return ExcelUtils.parseCell(cell);
    }

    public String getCellValueAsString(Cell cell) {
        return ExcelUtils.parseCell(cell);
    }


    public List<String> getCellValues(List<String> cellAddresses) {
        return cellAddresses
                .stream()
                .map(this::getCellValueAsString).collect(Collectors.toList());
    }

    @SneakyThrows
    public Map<String, Object> getCellValues(Map<String, Object> addressMap) {
        Map<String, Object> result = new HashMap<>();
        this.handleMap(result, "data", addressMap);
        return result;
    }

    private void handleMap(Map<String, Object> result, String keyAddress, Map<String, Object> addressMap) {
        Map<String, Object> childResult = new HashMap<>();
        addressMap.forEach((key, value) -> {
            if (value instanceof String) {
                String cellAddress = (String) value;
                childResult.put(key, this.getCellValueAsString(cellAddress));
            }

            if (value instanceof ArrayList) {
                List<String> cellAddresses = (List<String>) value;
                childResult.put(key, this.getCellValues(cellAddresses));
            }

            if (value instanceof LinkedTreeMap) {
                Map<String, Object> valMap = (Map<String, Object>) value;
                this.handleMap(childResult, key, valMap);
            }
        });
        result.put(keyAddress, childResult);
    }

    /**
     *
     * @param startTableAddress
     * @return data of excel table
     * - Sample of return data
     * {
     *     "headers": [
     *          {
     *              "name": "Ngày hạch toán",
     *              "address": "A1",
     *          }
     *     ],
     *     "data": []
     * }
     */
    public Map<String, Object> readTable(String startTableAddress) {
        Cell startCell = this.getCell(startTableAddress);
        Row startRow = startCell.getRow();

        List<Map<String, Object>> headers = new ArrayList<>();
        Map<Integer, String> headerIndex = new HashMap<>();
        for (int i = startCell.getColumnIndex(); i < startRow.getLastCellNum(); i++) {
            String headerVal = ExcelUtils.parseCell(startRow.getCell(i));
            headerIndex.put(i, headerVal);
            headers.add(MapUtils.ImmutableMap()
                            .put("name", ParserUtils.isNullOrEmpty(headerVal) ? "" : headerVal)
                            .put("address", new CellReference(startCell.getRowIndex(), i).formatAsString())
                    .build());
        }

        List<Map<String, Object>> records = new ArrayList<>();

        for (Row currentRow : this.sheet) {
            if (currentRow.getRowNum() <= startRow.getRowNum()) {
                continue;
            }
            Map<String, Object> record = new HashMap<>();
            for (Cell cell : currentRow) {
                record.put(
                        headerIndex.get(cell.getColumnIndex()),
                        this.getCellValueAsString(cell)
                );
            }
            records.add(record);
        }

        return MapUtils.ImmutableMap()
                .put("headers", headers)
                .put("records", records)
                .build();
    }


    public Cell getCell(String cellAddress) {
        CellReference cellReference = new CellReference(cellAddress);
        return this.sheet
                .getRow(cellReference.getRow())
                .getCell(cellReference.getCol());
    }
}
