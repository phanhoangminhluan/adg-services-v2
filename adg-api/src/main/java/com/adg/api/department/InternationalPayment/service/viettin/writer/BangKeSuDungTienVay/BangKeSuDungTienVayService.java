package com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeSuDungTienVay;

import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelTable;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelUtils;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelWriter;
import com.adg.api.department.InternationalPayment.service.bidv.enums.HoaDonHeaderMetadata;
import com.adg.api.department.InternationalPayment.service.viettin.reader.ToKhaiHaiQuanHeaderInfoMetadata;
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
 * Created on: 2022.05.29 08:10
 */
public class BangKeSuDungTienVayService {

    private ExcelWriter excelWriter;
    private ExcelTable excelTable;
    private Map<String, Object> data;
    private String outputFolder;
    private ZonedDateTime fileDate;
    private double totalCost = 0;

    public BangKeSuDungTienVayService(String outputFolder, List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> toKhaiHaiQuanRecords, ZonedDateTime fileDate, InputStream inputStream) {
        this.outputFolder = outputFolder;
        this.excelWriter = new ExcelWriter(inputStream);
        this.excelWriter.openSheet();
        this.excelTable = new ExcelTable(this.excelWriter, AdgExcelTableHeaderMetadata.getViettinBangKeSuDungTienVay());
        this.fileDate = fileDate;
        this.data = this.transformHoaDonRecords(hoaDonRecords, toKhaiHaiQuanRecords);
    }

    public void exportDocument() {
        this.insertRecordToTable();
        this.excelTable.removeSampleRow();
        this.fillData();
        this.build();
    }

    private Map<String, Object> transformHoaDonRecords(List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> toKhaiHaiQuanRecords) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> bangKe = new ArrayList<>();

        int stt = 1;


        for (Map<String, Object> hoaDonRecord : hoaDonRecords) {
            Map<String, Object> transformedRecord = new HashMap<>();
            for (BangKeSuDungTienVayHeaderInfoMetadata headerInfoMetadata : BangKeSuDungTienVayHeaderInfoMetadata.values()) {
                if (headerInfoMetadata == BangKeSuDungTienVayHeaderInfoMetadata.TT) {
                    transformedRecord.put(headerInfoMetadata.getHeaderName(), stt);
                } else {
                    transformedRecord.put(headerInfoMetadata.getHeaderName(), headerInfoMetadata.transformHoaDonCallback.apply(hoaDonRecord));
                }
            }
            stt++;
            totalCost += MapUtils.getDouble(hoaDonRecord, HoaDonHeaderMetadata.TongTienThanhToan.deAccentedName);
            bangKe.add(transformedRecord);
        }

        for (Map<String, Object> toKhaiHaiQuanRecord : toKhaiHaiQuanRecords) {
            Map<String, Object> transformedRecord = new HashMap<>();
            for (BangKeSuDungTienVayHeaderInfoMetadata headerInfoMetadata : BangKeSuDungTienVayHeaderInfoMetadata.values()) {
                if (headerInfoMetadata == BangKeSuDungTienVayHeaderInfoMetadata.TT) {
                    transformedRecord.put(headerInfoMetadata.getHeaderName(), stt);
                } else {
                    transformedRecord.put(headerInfoMetadata.getHeaderName(), headerInfoMetadata.transformToKhaiHaiQuanCallback.apply(toKhaiHaiQuanRecord));
                }
            }
            stt++;
            totalCost += MapUtils.getDouble(toKhaiHaiQuanRecord, ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.deAccentedName);

            bangKe.add(transformedRecord);
        }
        result.put("Bảng kê", bangKe);
        return result;
    }
    private void insertRecordToTable() {
        List<Map<String, Object>> records = MapUtils.getListMapStringObject(this.data, "Bảng kê");
        records.forEach(item -> this.excelTable.insert(item));
        this.excelTable.merge();
    }
    private void build() {
        String fileName = String.format("Bảng kê sử dụng tiền vay - %s.xlsx", DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_03));
        this.excelWriter.build(outputFolder + "/" + fileName);
    }

    private void fillData() {
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

        Cell soTienNhanNoHeaderCell = this.excelWriter.getCell(BangKeSuDungTienVayHeaderInfoMetadata.SoTienNhanNo.getCellAddress());
        startCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soTienNhanNoHeaderCell.getRowIndex() + 1),
                soTienNhanNoHeaderCell.getColumnIndex()
        ).getAddress().formatAsString();

        endCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soTienNhanNoHeaderCell.getRowIndex() + this.excelTable.getSize()),
                soTienNhanNoHeaderCell.getColumnIndex()
        ).getAddress().formatAsString();

        tongCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soTienNhanNoHeaderCell.getRowIndex() + this.excelTable.getSize() + 1),
                soTienNhanNoHeaderCell.getColumnIndex()
        );

        Cell signDateCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soTienNhanNoHeaderCell.getRowIndex() + this.excelTable.getSize() + 5),
                soTienNhanNoHeaderCell.getColumnIndex()
        );

        ExcelUtils.setCell(tongCell, String.format("SUM(%s:%s)", startCell, endCell), CellType.FORMULA);
        ExcelUtils.setCell(signDateCell, String.format("TPHCM, ngày %s tháng %s năm %s", this.fileDate.getDayOfMonth(), this.fileDate.getMonthValue(), this.fileDate.getYear()), CellType.STRING);

        Cell taiKhoanHeaderCell = this.excelWriter.getCell(BangKeSuDungTienVayHeaderInfoMetadata.TaiKhoan.getCellAddress());

        Cell soTienNhanNoCell = this.excelWriter.getCell(
                this.excelWriter.getRow(taiKhoanHeaderCell.getRowIndex() + this.excelTable.getSize() + 2),
                taiKhoanHeaderCell.getColumnIndex()
        );

        Cell soTienChuyenKhoanCell = this.excelWriter.getCell(
                this.excelWriter.getRow(taiKhoanHeaderCell.getRowIndex() + this.excelTable.getSize() + 4),
                taiKhoanHeaderCell.getColumnIndex()
        );

        ExcelUtils.setCell(soTienNhanNoCell, totalCost, CellType.NUMERIC);
        ExcelUtils.setCell(soTienChuyenKhoanCell, totalCost, CellType.NUMERIC);

    }

}
