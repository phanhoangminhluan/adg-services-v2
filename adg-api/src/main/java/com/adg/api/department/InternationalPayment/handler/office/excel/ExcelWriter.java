package com.adg.api.department.InternationalPayment.handler.office.excel;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.21 22:53
 */
public class ExcelWriter {

    private String filePath;
    private Workbook workbook;
    private FileInputStream fileInputStream;
    private Sheet sheet;

    @SneakyThrows
    public ExcelWriter(String filePath) {
        this.filePath = filePath;
        this.fileInputStream = new FileInputStream(filePath);
        this.workbook = new XSSFWorkbook(fileInputStream);
    }

    public void openSheet() {
        this.sheet = this.workbook.getSheetAt(0);
    }

    public void setCellValue(String cellAddress, String cellValue) {
        Cell cell = this.getCell(cellAddress);
        cell.setCellValue(cellValue);
    }

    public void copyCellValue(Cell sourceCell, Cell targetCell) {
        targetCell.setCellValue(ExcelUtils.parseString(sourceCell));
    }

    public Row insertBelow(Row row) {
        this.sheet.shiftRows(row.getRowNum() + 1, this.sheet.getLastRowNum(), 1);
        return this.sheet.createRow(row.getRowNum() + 1);
    }

    public Row cloneRowSetting(Row sourceRow, Row targetRow, int startColumnIndex, int endColumnIndex) {
        for (int i = startColumnIndex; i <= endColumnIndex; i++) {
            Cell targetCell = this.getCell(targetRow, i);
            Cell sourceCell = this.getCell(sourceRow, i);
            targetCell.setCellStyle(sourceCell.getCellStyle());
        }
        return targetRow;
    }

    public Cell getCell(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell != null) {
            return cell;
        }
        return row.createCell(columnIndex);
    }

    public Row getRow(int rowIndex) {
        Row row = this.sheet.getRow(rowIndex);
        if (row != null) {
            return row;
        }

        return this.sheet.createRow(rowIndex);
    }


    public List<Cell> getCellsByRange(String startCellAddress, int columnSize) {
        Cell startCell = this.getCell(startCellAddress);
        int endCellIndex = startCell.getColumnIndex() + columnSize - 1;

        Cell endCell = startCell.getRow().getCell(endCellIndex) != null
                ? startCell.getRow().getCell(endCellIndex)
                : startCell.getRow().createCell(endCellIndex);

        List<Cell> cells = new ArrayList<>();

        for (Cell cell : startCell.getRow()) {
            if (cell.getColumnIndex() < startCell.getColumnIndex()) continue;
            if (cell.getColumnIndex() > endCell.getColumnIndex()) break;

            cells.add(cell);
        }

        return cells;
    }

    @SneakyThrows
    public void build(String path) {
        this.workbook.write(new FileOutputStream(path));

    }


    public Cell getCell(String cellAddress) {
        CellReference cellReference = new CellReference(cellAddress);
        Row row = this.getRow(cellReference.getRow());
        return this.getCell(row, cellReference.getCol());
    }

    public void removeRow(Row row, int startIndexColumn, int endIndexColumn) {
        this.sheet.shiftRows(row.getRowNum() + 1, this.sheet.getLastRowNum(), -1);
    }

    public void setShiftCellValue(String originalAddress, int aboveTableSize, Object value, CellType cellType) {

        Cell originalCell = this.getCell(originalAddress);
        Cell shiftedCell = this.getCell(
                this.getRow(aboveTableSize + originalCell.getRowIndex() - 1),
                originalCell.getColumnIndex()
        );

        ExcelUtils.setCell(shiftedCell, value, cellType);
    }

    public Sheet getSheet() {
        return sheet;
    }
}
