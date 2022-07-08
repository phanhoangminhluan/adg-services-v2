package com.adg.api.department.InternationalPayment.disbursement.office.excel;

import com.adg.api.department.InternationalPayment.disbursement.office.AdgExcelTableHeaderInfo;
import com.adg.api.department.InternationalPayment.disbursement.office.AdgExcelTableHeaderMetadata;
import com.google.common.collect.Comparators;
import lombok.Getter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.24 11:12
 */
@Getter
public class ExcelTable {

    private final AdgExcelTableHeaderMetadata metadata;
    private final Cell startCell;
    private final Row sampleRow;
    private final ExcelWriter excelWriter;
    private int size;
    private final int startColumnIndex;
    private final int endColumnIndex;
    private final Map<Integer, AdgExcelTableHeaderInfo> headerNameIndexMap;


    public ExcelTable(ExcelWriter excelWriter, AdgExcelTableHeaderMetadata metadata) {
        this.metadata = metadata;
        this.excelWriter = excelWriter;
        this.startCell = this.excelWriter.getCell(metadata.getStartCellAddress());
        this.sampleRow = this.startCell.getRow();
        this.startColumnIndex = this.startCell.getColumnIndex();
        this.endColumnIndex = this.startColumnIndex + this.metadata.getColumnSize() - 1;

        this.headerNameIndexMap = new HashMap<>();
        for (AdgExcelTableHeaderInfo header : this.metadata.getHeaders()) {
            Cell cell = this.excelWriter.getCell(header.getCellAddress());
            this.headerNameIndexMap.put(cell.getColumnIndex(), header);
        }
    }

    public Row insert(Map<String, Object> item) {

        Row lastRow = this.excelWriter.getSheet().getRow(sampleRow.getRowNum() + size);
        size++;
        Row currentRow = this.excelWriter.insertBelow(lastRow);
        currentRow = this.excelWriter.cloneRowSetting(lastRow, currentRow , this.startColumnIndex, this.endColumnIndex);
       for (int i = this.startColumnIndex; i <= this.endColumnIndex; i++) {
           AdgExcelTableHeaderInfo header = this.headerNameIndexMap.get(i);
           Cell cell = currentRow.getCell(i);
           Object value = item.get(header.getHeaderName());
           if (value != null) {
               ExcelUtils.setCell(cell, value, header.getCellType());
           }

       }
       return currentRow;
    }

    /**
     * SmellCode.
     * @TODO: Check this later
     * @param item
     * @return
     */
    public Row insert2(Map<String, Object> item) {

        Row lastRow = this.excelWriter.getSheet().getRow(sampleRow.getRowNum() + size);
        size++;
        Row currentRow = this.excelWriter.insertBelow(lastRow);
        int max = this.headerNameIndexMap.keySet().stream().max(Comparators::max).get();
        int min = this.headerNameIndexMap.keySet().stream().min(Comparators::min).get();
        currentRow = this.excelWriter.cloneRowSetting(lastRow, currentRow , 0, 20);
        Cell preCell = null;
        for (Integer i : this.headerNameIndexMap.keySet()) {
            AdgExcelTableHeaderInfo header = this.headerNameIndexMap.get(i);
            Cell cell = this.excelWriter.getCell(currentRow, i);

            if (preCell != null) {
                if (preCell.getColumnIndex() != i - 1) {
                    this.excelWriter.mergeCell(preCell, this.excelWriter.getCell(preCell.getRow(), i - 1));

                    int startI = preCell.getColumnIndex();
                    int endI = i - 1;
                    for (int j = startI; j <= endI; j++) {
                        this.excelWriter.getCell(preCell.getRow(), j).getCellStyle().setBorderBottom(BorderStyle.THIN);
                    }

                    preCell.getCellStyle().setBorderBottom(BorderStyle.THIN);
                    preCell.getCellStyle().setBorderLeft(BorderStyle.THIN);
                    preCell.getCellStyle().setBorderRight(BorderStyle.THIN);
                    preCell.getCellStyle().setBorderTop(BorderStyle.THIN);
                }
            }
            Object value = item.get(
                    header.getHeaderName()
            );
            if (value != null) {
                ExcelUtils.setCell(cell, value, header.getCellType());
            }
            preCell = cell;

        }
        return currentRow;
    }

    public void merge() {
        Row currentRow = this.excelWriter.getSheet().getRow(sampleRow.getRowNum() + 1);
        int startRow = currentRow.getRowNum() + 1;
        for (int columnIndex = this.startColumnIndex; columnIndex <= this.endColumnIndex; columnIndex++) {
            AdgExcelTableHeaderInfo header = this.headerNameIndexMap.get(columnIndex);
            if (header.isGroupedColumn()) {
                Cell startMergeRegion = this.excelWriter.getCell(currentRow, columnIndex);
                String startMergeValue = ExcelUtils.parseString(startMergeRegion);
                Cell currentCell = null;
                String currentValue = null;
                for (int rowIndex = startRow; rowIndex <= sampleRow.getRowNum() + size; rowIndex++) {
                    currentCell = this.excelWriter.getCell(this.excelWriter.getRow(rowIndex), columnIndex);
                    currentValue = ExcelUtils.parseString(currentCell);
                    if (currentValue == null || !currentValue.equals(startMergeValue)) {
                        startMergeValue = currentValue;
                        if (!startMergeRegion.getAddress().formatAsString().equals(this.excelWriter.getAboveCell(currentCell).getAddress().formatAsString())) {
                            this.excelWriter.mergeCell(startMergeRegion, this.excelWriter.getAboveCell(currentCell));
                        }
                        startMergeRegion = currentCell;
                    }
                }
                if (currentCell != null) {
                    if (!startMergeRegion.getAddress().formatAsString().equals(currentCell.getAddress().formatAsString())) {
                        this.excelWriter.mergeCell(startMergeRegion, currentCell);
                    }
                }
            }
        }
    }

    public void removeSampleRow() {
        this.excelWriter.removeRow(this.sampleRow, startColumnIndex, endColumnIndex);
    }



}
