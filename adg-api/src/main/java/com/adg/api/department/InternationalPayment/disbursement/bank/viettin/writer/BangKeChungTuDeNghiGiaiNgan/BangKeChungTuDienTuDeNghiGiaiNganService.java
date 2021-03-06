package com.adg.api.department.InternationalPayment.disbursement.bank.viettin.writer.BangKeChungTuDeNghiGiaiNgan;

import com.adg.api.department.InternationalPayment.disbursement.office.AdgExcelTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.disbursement.office.excel.ExcelTable;
import com.adg.api.department.InternationalPayment.disbursement.office.excel.ExcelUtils;
import com.adg.api.department.InternationalPayment.disbursement.office.excel.ExcelWriter;
import com.adg.api.department.InternationalPayment.disbursement.reader.header.HoaDonHeaderMetadata;
import com.adg.api.department.InternationalPayment.disbursement.reader.header.ToKhaiHaiQuanHeaderInfoMetadata;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.core.io.Resource;

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
@Log4j2
public class BangKeChungTuDienTuDeNghiGiaiNganService {

    private final ExcelWriter excelWriter;
    private final ExcelTable excelTable;
    private final Map<String, Object> data;
    private final String outputFolder;
    private final ZonedDateTime fileDate;

    public BangKeChungTuDienTuDeNghiGiaiNganService(String outputFolder, List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> toKhaiHaiQuanRecords, ZonedDateTime fileDate, InputStream templateInputStream) {

        this.outputFolder = outputFolder;
        this.excelWriter = new ExcelWriter(templateInputStream);
        this.excelWriter.openSheet();
        this.excelTable = new ExcelTable(
                this.excelWriter,
                AdgExcelTableHeaderMetadata.getBangKeChungTuDienTuDeNghiGiaiNgan()
        );
        this.fileDate = fileDate;
        this.data = this.transformRecords(hoaDonRecords, toKhaiHaiQuanRecords);
    }

    @SneakyThrows
    public static Map<String, Object> writeOut(String outputFolder, List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> toKhaiHaiQuanRecords, ZonedDateTime fileDate, Resource resource) {
        long t1 = System.currentTimeMillis();
        if (toKhaiHaiQuanRecords.isEmpty() && hoaDonRecords.isEmpty()) {
            return MapUtils.ImmutableMap()
                    .put("step", "Generate 'B???ng K?? Ch???ng T??? ??i???n T??? ????? Ngh??? Gi???i Ng??n'")
                    .put("duration", "0s")
                    .put("detail", List.of())
                    .build();
        }
        Map<String, Object> stats = new BangKeChungTuDienTuDeNghiGiaiNganService(outputFolder, hoaDonRecords, toKhaiHaiQuanRecords, fileDate, resource.getInputStream())
                .exportDocuments();
        return MapUtils.ImmutableMap()
                .put("step", "Generate 'B???ng K?? Ch???ng T??? ??i???n T??? ????? Ngh??? Gi???i Ng??n'")
                .put("duration", DateTimeUtils.getRunningTimeInSecond(t1))
                .put("detail", List.of(stats))
                .build();
    }

    private Map<String, Object> transformRecords(List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> toKhaiHaiQuanRecords) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> danhSachChungTu = new ArrayList<>();
        int stt = 1;
        for (Map<String, Object> toKhaiHaiQuanRecord : toKhaiHaiQuanRecords) {
            String ncc = MapUtils.getString(toKhaiHaiQuanRecord, ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.deAccentedName);
            String soChungTu = MapUtils.getString(toKhaiHaiQuanRecord, ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.deAccentedName);
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
                            .put(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.GhiChu.getHeaderName(), "Ho?? ????n")
                            .build()
            );
            stt++;
        }
        result.put("danhSachChungTu", danhSachChungTu);
        return result;
    }

    private Map<String, Object> exportDocuments() {
        Map<String, Object> stats = new HashMap<>();

        try {
            long t1 = System.currentTimeMillis();
            this.insertRecordToTable();
            stats.put("fillTableDuration", DateTimeUtils.getRunningTimeInSecond(t1));

            t1 = System.currentTimeMillis();
            this.fillSum();
            this.fillSignDate();
            stats.put("fillOtherDataDuration", DateTimeUtils.getRunningTimeInSecond(t1));

            t1 = System.currentTimeMillis();
            String fileName = this.build();
            stats.put("fileName", fileName);
            stats.put("writeFileDuration", DateTimeUtils.getRunningTimeInSecond(t1));
        } finally {
            log.info("Step: {}. File name: {}. Fill table duration: {}. Fill other data duration: {}. Write file duration: {}",
                    "Generate 'B???ng K?? Ch???ng T??? ??i???n T??? ????? Ngh??? Gi???i Ng??n'",
                    MapUtils.getString(stats, "fileName"),
                    MapUtils.getString(stats, "fillTableDuration"),
                    MapUtils.getString(stats, "fillOtherDataDuration"),
                    MapUtils.getString(stats, "writeFileDuration")
            );
        }
        return stats;
    }

    private void insertRecordToTable() {
        List<Map<String, Object>> records = MapUtils.getListMapStringObject(this.data, "danhSachChungTu");
        records.forEach(this.excelTable::insert);
        this.excelTable.merge();
        this.excelTable.removeSampleRow();

    }

    private void fillSignDate() {
        String originalCellAddress = "D18";
        Cell originalCell = this.excelWriter.getCell(originalCellAddress);
        Cell shiftedCell = this.excelWriter
                .getCell(
                        this.excelWriter.getRow(this.excelTable.getSize() + originalCell.getRowIndex() - 1),
                        originalCell.getColumnIndex()
                );
        String val = String.format("TP HCM, ng??y %s th??ng %s n??m %s", fileDate.getDayOfMonth(), fileDate.getMonth().getValue(), fileDate.getYear());
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

    private String build() {
        String fileName = String.format("B???ng k?? ch???ng t??? ??i???n t??? ????? ngh??? gi???i ng??n - %s.xlsx",
                DateTimeUtils.convertZonedDateTimeToFormat(fileDate, "UTC", DateTimeUtils.FMT_03)
        );
        this.excelWriter.build(this.outputFolder + "/" + fileName);
        return fileName;
    }
}
