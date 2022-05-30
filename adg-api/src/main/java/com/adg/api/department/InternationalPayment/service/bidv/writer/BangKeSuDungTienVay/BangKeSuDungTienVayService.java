package com.adg.api.department.InternationalPayment.service.bidv.writer.BangKeSuDungTienVay;

import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelTable;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelUtils;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelWriter;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.30 13:28
 */
public class BangKeSuDungTienVayService {

    private final ExcelWriter excelWriter;
    private final ExcelTable excelTable;
    private final Map<String, Object> data;
    private final String outputFolder;
    private final ZonedDateTime fileDate;
    private final String contractNumber;

    public BangKeSuDungTienVayService(String outputFolder, List<Map<String, Object>> hoaDonRecords, ZonedDateTime fileDate, String contractNumber, InputStream inputStream) {
        this.outputFolder = outputFolder;
        this.excelWriter = new ExcelWriter(inputStream);
        this.excelWriter.openSheet();
        this.excelTable = new ExcelTable(this.excelWriter, AdgExcelTableHeaderMetadata.getBidvBangKeSuDungTienVay());
        this.fileDate = fileDate;
        this.contractNumber = contractNumber;
        this.data = this.transformHoaDonRecords(hoaDonRecords);
    }

    public Map<String, Object> transformHoaDonRecords(List<Map<String, Object>> hoaDonRecords) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> bangKe = new ArrayList<>();

        for (Map<String, Object> hoaDonRecord : hoaDonRecords) {
            Map<String, Object> transformedRecord = new HashMap<>();
            for (BangKeSuDungTienVayHeaderInfoMetadata headerInfoMetadata : BangKeSuDungTienVayHeaderInfoMetadata.values()) {
                transformedRecord.put(headerInfoMetadata.getHeaderName(), headerInfoMetadata.transformCallback.apply(hoaDonRecord));
            }
            bangKe.add(transformedRecord);
        }
        result.put("Bảng kê", bangKe);
        return result;
    }

    public void insertRecordToTable() {
        List<Map<String, Object>> records = MapUtils.getListMapStringObject(this.data, "Bảng kê");
        records.forEach(item -> this.excelTable.insert(item));
    }

    public void exportDocument() {
        this.insertRecordToTable();
        this.excelTable.removeSampleRow();
        this.fillSum();
        this.build();
    }

    private void build() {
        String fileName = String.format("Bảng kê sử dụng tiền vay - %s.xlsx", DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_03));
        this.excelWriter.build(outputFolder + "/" + fileName);
    }

    private void fillSum() {
        Cell soTienHeaderCell = this.excelWriter.getCell(BangKeSuDungTienVayHeaderInfoMetadata.SoTien.getCellAddress());
        String startCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soTienHeaderCell.getRowIndex() + 1),
                soTienHeaderCell.getColumnIndex()
        ).getAddress().formatAsString();

        String endCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soTienHeaderCell.getRowIndex() + this.excelTable.getSize()),
                soTienHeaderCell.getColumnIndex()
        ).getAddress().formatAsString();

        Cell tongCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soTienHeaderCell.getRowIndex() + this.excelTable.getSize() + 1),
                soTienHeaderCell.getColumnIndex()
        );

        ExcelUtils.setCell(tongCell, String.format("SUM(%s:%s)", startCell, endCell), CellType.FORMULA);

        ExcelUtils.setCell(
                this.excelWriter.getCell("A7"),
                String.format("Chi tiết nội dung sử dụng tiền vay theo hợp đồng tín dụng ngắn hạn cụ thể số : 01.%s/2021/8088928/HĐTD ngày %s được ký kết giữa Ngân hàng và Bên vay.", contractNumber, DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_09)),
                CellType.STRING);

        Cell sttHeaderCell = this.excelWriter.getCell(BangKeSuDungTienVayHeaderInfoMetadata.TT.getCellAddress());

        Cell finalCell = this.excelWriter.getCell(
                this.excelWriter.getRow(sttHeaderCell.getRowIndex() + this.excelTable.getSize() + 3),
                sttHeaderCell.getColumnIndex()
        );

        ExcelUtils.setCell(
                finalCell,
                String.format("Bảng kê này là một bộ phận trong thể tách rời hợp đồng tín dụng ngắn hạn cụ thể số 01.%s/2021/8088928/HĐTD ngày %s được ký kết giữa Ngân hàng và Bên vay.", contractNumber, DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_09)),
                CellType.STRING);
    }

}
