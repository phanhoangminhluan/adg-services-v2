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

    private ExcelWriter excelWriter;
    private ExcelTable excelTable;
    private Map<String, Object> data;
    private String outputFolder;

    public BangKeSuDungTienVayService(String outputFolder, List<Map<String, Object>> hoaDonRecords, InputStream inputStream) {
        this.outputFolder = outputFolder;
        this.excelWriter = new ExcelWriter(inputStream);
        this.excelWriter.openSheet();
        this.excelTable = new ExcelTable(this.excelWriter, AdgExcelTableHeaderMetadata.getBangKeSuDungTienVay());
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
        this.fillDescription();
        this.fillAdditionalDescription();
        this.build();
    }

    private void build() {
        String fileName = String.format("Bảng kê sử dụng tiền vay - %s.xlsx", DateTimeUtils.convertZonedDateTimeToFormat(ZonedDateTime.now(), "Asia/Ho_Chi_Minh", DateTimeUtils.MA_DATE_TIME_FORMATTER));
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
    }

    private void fillDescription() {
        String description = String.format(
                "Chi tiết nội dung sử dụng tiền vay theo hợp đồng tín dụng ngắn hạn cụ thể số : 01.219/2021/8088928/HĐTD ngày %s được ký kết giữa Ngân hàng và Bên vay.",
                DateTimeUtils.convertZonedDateTimeToFormat(ZonedDateTime.now(), "Asia/Ho_Chi_Minh", DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_09))
        );
        String originalCellAddress = "A7";
        Cell originalCell = this.excelWriter.getCell(originalCellAddress);
        originalCell.setCellValue(description);
    }

    private void fillAdditionalDescription() {
        String description = String.format(
                "Bảng kê này là một bộ phận trong thể tách rời hợp đồng tín dụng ngắn hạn cụ thể số 01.219/2021/8088928/HĐTD ngày %s được ký kết giữa Ngân hàng và Bên vay.",
                DateTimeUtils.convertZonedDateTimeToFormat(ZonedDateTime.now(), "Asia/Ho_Chi_Minh", DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_09))
        );

        String originalCellAddress = "A13";

        this.excelWriter.setShiftCellValue(originalCellAddress, this.excelTable.getSize(), description, CellType.STRING);
    }

}
