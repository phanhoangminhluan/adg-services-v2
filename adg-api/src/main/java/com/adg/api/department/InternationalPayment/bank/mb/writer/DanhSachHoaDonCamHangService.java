package com.adg.api.department.InternationalPayment.bank.mb.writer;

import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelTable;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelUtils;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelWriter;
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
 * Created on: 2022.06.21 17:35
 */
@Log4j2
public class DanhSachHoaDonCamHangService {

    private final ExcelWriter excelWriter;
    private final ExcelTable excelTable;
    private final Map<String, Object> data;
    private final String outputFolder;
    private final ZonedDateTime fileDate;

    public DanhSachHoaDonCamHangService(String outputFolder, Map<String, Object> phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon, ZonedDateTime fileDate, InputStream templateInputStream) {
        this.outputFolder = outputFolder;
        this.excelWriter = new ExcelWriter(templateInputStream);
        this.excelWriter.openSheet();
        this.excelTable = new ExcelTable(this.excelWriter, AdgExcelTableHeaderMetadata.getDanhSachHoaDonCamHang());
        this.fileDate = fileDate;
        this.data = this.transformPhieuNhapKhoRecords(phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon);
    }

    @SneakyThrows
    public static Map<String, Object> writeOut(
            String outputFolder,
            Map<String, Object> phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon,
            ZonedDateTime fileDate,
            Resource resource
    ) {

        long t1 = System.currentTimeMillis();

        List<Map<String, Object>> statsList = new ArrayList<>();
        Map<String, Object> stats = new DanhSachHoaDonCamHangService(outputFolder, phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon, fileDate, resource.getInputStream()).exportDocument();
        statsList.add(stats);

        return MapUtils.ImmutableMap()
                .put("step", "Generate 'Danh sách hoá đơn'")
                .put("duration", DateTimeUtils.getRunningTimeInSecond(t1))
                .put("detail", statsList)
                .build();

    }

    private Map<String, Object> transformPhieuNhapKhoRecords(Map<String, Object>  phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> table = new ArrayList<>();

        for (String nhaCungCap : phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon.keySet()) {
            Map<String, Object> phieuNhapKhoRecordsGroupBySoHoaDon = MapUtils.getMapStringObject(phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon, nhaCungCap);
            for (String soHoaDon : phieuNhapKhoRecordsGroupBySoHoaDon.keySet()) {
                List<Map<String, Object>> phieuNhapKhoRecords = MapUtils.getListMapStringObject(phieuNhapKhoRecordsGroupBySoHoaDon, soHoaDon);
                for (Map<String, Object> phieuNhapKhoRecord : phieuNhapKhoRecords) {
                    Map<String, Object> record = new HashMap<>();
                    for (DanhSachHoaDonCamHangHeaderInfoMetadata header : DanhSachHoaDonCamHangHeaderInfoMetadata.values()) {
                        record.put(header.getHeaderName(), header.transformCallback.apply(phieuNhapKhoRecord));
                    }
                    table.add(record);
                }
            }
        }

        result.put("Danh sách hoá đơn", table);
        return result;
    }

    private void fillData() {
        Cell soLuongCamHangHeaderCell = this.excelWriter.getCell(DanhSachHoaDonCamHangHeaderInfoMetadata.SoLuongCamHang.getCellAddress());
        String startCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soLuongCamHangHeaderCell.getRowIndex() + 1),
                soLuongCamHangHeaderCell.getColumnIndex()
        ).getAddress().formatAsString();

        String endCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soLuongCamHangHeaderCell.getRowIndex() + this.excelTable.getSize()),
                soLuongCamHangHeaderCell.getColumnIndex()
        ).getAddress().formatAsString();

        Cell tongCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soLuongCamHangHeaderCell.getRowIndex() + this.excelTable.getSize() + 1),
                soLuongCamHangHeaderCell.getColumnIndex()
        );

        ExcelUtils.setCell(tongCell, String.format("SUM(%s:%s)", startCell, endCell), CellType.FORMULA);


        Cell soTienGhiNhanHeaderCell = this.excelWriter.getCell(DanhSachHoaDonCamHangHeaderInfoMetadata.SoTienGn.getCellAddress());
        startCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soTienGhiNhanHeaderCell.getRowIndex() + 1),
                soTienGhiNhanHeaderCell.getColumnIndex()
        ).getAddress().formatAsString();

        endCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soTienGhiNhanHeaderCell.getRowIndex() + this.excelTable.getSize()),
                soTienGhiNhanHeaderCell.getColumnIndex()
        ).getAddress().formatAsString();

        tongCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soTienGhiNhanHeaderCell.getRowIndex() + this.excelTable.getSize() + 1),
                soTienGhiNhanHeaderCell.getColumnIndex()
        );

        ExcelUtils.setCell(tongCell, String.format("SUM(%s:%s)", startCell, endCell), CellType.FORMULA);

    }

    private void insertRecordToTable() {
        List<Map<String, Object>> records = MapUtils.getListMapStringObject(this.data, "Danh sách hoá đơn");
        records.forEach(this.excelTable::insert);
        this.excelTable.merge();
        this.excelTable.removeSampleRow();
    }

    private String build() {
        String fileName = String.format("Danh sách hoá đơn cầm hàng - %s.xlsx",
                DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_03)
        );
        this.excelWriter.build(this.outputFolder + "/" + fileName);
        return fileName;
    }

    public Map<String, Object> exportDocument() {
        Map<String, Object> stats = new HashMap<>();

        try {
            long t1 = System.currentTimeMillis();
            this.insertRecordToTable();
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
                    "Generate 'Bảng Kê Nộp Thuế'",
                    MapUtils.getString(stats, "fileName"),
                    MapUtils.getString(stats, "fillTableDuration"),
                    MapUtils.getString(stats, "fillOtherDataDuration"),
                    MapUtils.getString(stats, "writeFileDuration")
            );
        }
        return stats;
    }
}
