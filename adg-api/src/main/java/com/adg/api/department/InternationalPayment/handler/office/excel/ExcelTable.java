package com.adg.api.department.InternationalPayment.handler.office.excel;

import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderInfo;
import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderMetadata;
import lombok.Getter;
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

    public void removeSampleRow() {
        this.excelWriter.removeRow(this.sampleRow, startColumnIndex, endColumnIndex);
    }



}
