package com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeChungTuDeNghiGiaiNgan;

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
 * Created on: 2022.04.24 15:18
 */
public class BangKeChungTuDienTuDeNghiGiaiNganService {

    private final ExcelWriter excelWriter;
    private final ExcelTable excelTable;
    private final Map<String, Object> data;
    private final String outputFolder;
    private final ZonedDateTime fileDate;

    public BangKeChungTuDienTuDeNghiGiaiNganService(String outputFolder, List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> toKhaiHaiQuanRecords, ZonedDateTime fileDate, InputStream inputStream) {

        this.outputFolder = outputFolder;
        this.excelWriter = new ExcelWriter(inputStream);
        this.excelWriter.openSheet();
        this.excelTable = new ExcelTable(
                this.excelWriter,
                AdgExcelTableHeaderMetadata.getBangKeChungTuDienTuDeNghiGiaiNgan()
        );
        this.fileDate = fileDate;
        this.data = this.transformRecords(hoaDonRecords, toKhaiHaiQuanRecords);
    }

    private Map<String, Object> transformRecords(List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> toKhaiHaiQuanRecords) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> danhSachChungTu = new ArrayList<>();
        int stt = 1;
        for (Map<String, Object> toKhaiHaiQuanRecord : toKhaiHaiQuanRecords) {
            String ncc = MapUtils.getString(toKhaiHaiQuanRecord, ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.deAccentedName);
            String soChungTu = MapUtils.getString(toKhaiHaiQuanRecord, ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.deAccentedName);
            String ngayChungTu = MapUtils.getString(toKhaiHaiQuanRecord, ToKhaiHaiQuanHeaderInfoMetadata.NgayDangKy.deAccentedName);
            String soTien = MapUtils.getString(toKhaiHaiQuanRecord, ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.deAccentedName);
            danhSachChungTu.add(
                    MapUtils.ImmutableMap()
                            .put(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.TT.getHeaderName(), stt)
                            .put(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.SoChungTu.getHeaderName(), soChungTu)
                            .put(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.NgayChungTu.getHeaderName(), ngayChungTu)
                            .put(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.SoTien.getHeaderName(), soTien)
                            .put(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.DonViPhatHanh.getHeaderName(), ncc)
                            .put(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.GhiChu.getHeaderName(), "TKHQ")
                            .build()
            );
            stt++;
        }

        for (Map<String, Object> hoaDonRecord : hoaDonRecords) {
            String ncc = MapUtils.getString(hoaDonRecord, HoaDonHeaderMetadata.NhaCungCap.deAccentedName);
            String soChungTu = MapUtils.getString(hoaDonRecord, HoaDonHeaderMetadata.SoHoaDon.deAccentedName);
            String ngayChungTu = MapUtils.getString(hoaDonRecord, HoaDonHeaderMetadata.NgayChungTu.deAccentedName);
            ngayChungTu = DateTimeUtils.reformatDate(ngayChungTu, DateTimeUtils.FMT_01, DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_09), "UTC", "UTC");
            String soTien = MapUtils.getString(hoaDonRecord, HoaDonHeaderMetadata.TongTienThanhToan.deAccentedName);
            danhSachChungTu.add(
                    MapUtils.ImmutableMap()
                            .put(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.TT.getHeaderName(), stt)
                            .put(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.SoChungTu.getHeaderName(), "0" + soChungTu)
                            .put(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.NgayChungTu.getHeaderName(), ngayChungTu)
                            .put(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.SoTien.getHeaderName(), soTien)
                            .put(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.DonViPhatHanh.getHeaderName(), ncc)
                            .put(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.GhiChu.getHeaderName(), "Hoá đơn")
                            .build()
            );
            stt++;
        }
        result.put("danhSachChungTu", danhSachChungTu);
        return result;
    }

    public void exportDocuments() {
        this.insertRecordToTable();
        this.fillSum();
        this.fillSignDate();
        this.build();
    }

    private void insertRecordToTable() {
        List<Map<String, Object>> records = MapUtils.getListMapStringObject(this.data, "danhSachChungTu");
        records.forEach(this.excelTable::insert);
        this.excelTable.merge();
        this.excelTable.removeSampleRow();

    }

    private void fillSignDate() {
        String originalCellAddress = "D17";
        Cell originalCell = this.excelWriter.getCell(originalCellAddress);
        Cell shiftedCell = this.excelWriter
                .getCell(
                        this.excelWriter.getRow(this.excelTable.getSize() + originalCell.getRowIndex() - 1),
                        originalCell.getColumnIndex()
                );
        String val = String.format("TP HCM, ngày %s tháng %s năm %s", fileDate.getDayOfMonth(), fileDate.getMonth().getValue(), fileDate.getYear());
        shiftedCell.setCellValue(val);
    }

    private void fillSum() {
        Cell soTienHeaderCell = this.excelWriter.getCell(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.SoTien.getCellAddress());
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

    private void build() {
        String fileName = String.format("Bảng kê chứng từ điện tử đề nghị giải ngân - %s.xlsx",
                DateTimeUtils.convertZonedDateTimeToFormat(fileDate, "UTC", DateTimeUtils.FMT_03)
        );
        this.excelWriter.build(this.outputFolder + "/" + fileName);
    }
}
