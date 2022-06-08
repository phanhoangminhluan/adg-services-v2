package com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeSuDungTienVay;

import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelTable;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelUtils;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelWriter;
import com.adg.api.department.InternationalPayment.service.bidv.enums.HoaDonHeaderMetadata;
import com.adg.api.department.InternationalPayment.service.viettin.reader.ToKhaiHaiQuanHeaderInfoMetadata;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.29 08:10
 */
@Log4j2
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
        this.data = this.transformRecords(hoaDonRecords, toKhaiHaiQuanRecords);
    }

    @SneakyThrows
    public static Map<String, Object> writeOut(String outputFolder, List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> toKhaiHaiQuanRecords, ZonedDateTime fileDate, Resource resource) {
        long t1 = System.currentTimeMillis();
        if (toKhaiHaiQuanRecords.isEmpty() && hoaDonRecords.isEmpty()) {
            return MapUtils.ImmutableMap()
                    .put("step", "Generate 'Bảng Kê Sử Dụng Tiền Vay'")
                    .put("duration", "0s")
                    .put("detail", List.of())
                    .build();
        }
        Map<String, Object> stats = new BangKeSuDungTienVayService(outputFolder, hoaDonRecords, toKhaiHaiQuanRecords, fileDate, resource.getInputStream())
                .exportDocument();
        return MapUtils.ImmutableMap()
                .put("step", "Generate 'Bảng Kê Sử Dụng Tiền Vay'")
                .put("duration", DateTimeUtils.getRunningTimeInSecond(t1))
                .put("detail", List.of(stats))
                .build();
    }

    public Map<String, Object> exportDocument() {
        Map<String, Object> stats = new HashMap<>();

        try {
            long t1 = System.currentTimeMillis();
            this.insertRecordToTable();
            this.excelTable.removeSampleRow();
            stats.put("fillTableDuration", DateTimeUtils.getRunningTimeInSecond(t1));

            t1 = System.currentTimeMillis();
            this.fillData();
            stats.put("fillOtherDataDuration", DateTimeUtils.getRunningTimeInSecond(t1));

            t1 = System.currentTimeMillis();
            String fileName = this.build();
            stats.put("fileName", fileName);
            stats.put("writeFileDuration", DateTimeUtils.getRunningTimeInSecond(t1));
        } finally {
            log.info("Step: {}. File name: {}. Fill table duration: {}. Fill other data duration: {}. Write file duration: {}",
                    "Generate 'Bảng Kê Sử Dụng Tiền Vay'",
                    MapUtils.getString(stats, "fileName"),
                    MapUtils.getString(stats, "fillTableDuration"),
                    MapUtils.getString(stats, "fillOtherDataDuration"),
                    MapUtils.getString(stats, "writeFileDuration")
            );
        }
        return stats;

    }

    private Map<String, Object> transformRecords(List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> toKhaiHaiQuanRecords) {
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

        List<Map<String, Object>> sortedToKhaiHaiQuan = new ArrayList<>(toKhaiHaiQuanRecords);

        sortedToKhaiHaiQuan.sort(Comparator.comparing(o -> MapUtils.getString(o, ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.deAccentedName)));

        for (Map<String, Object> toKhaiHaiQuanRecord : sortedToKhaiHaiQuan) {
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

    private void sortToKhaiHaiQuanByBank(List<Map<String, Object>> toKhaiHaiQuanRecords) {

    }

    private void insertRecordToTable() {
        List<Map<String, Object>> records = MapUtils.getListMapStringObject(this.data, "Bảng kê");
        records.forEach(item -> this.excelTable.insert(item));
        this.excelTable.merge();
    }
    private String build() {
        String fileName = String.format("Bảng kê sử dụng tiền vay - %s.xlsx", DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_03));
        this.excelWriter.build(outputFolder + "/" + fileName);
        return fileName;
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
                this.excelWriter.getRow(soTienHeaderCell.getRowIndex() + this.excelTable.getSize() + 5),
                soTienHeaderCell.getColumnIndex()
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
