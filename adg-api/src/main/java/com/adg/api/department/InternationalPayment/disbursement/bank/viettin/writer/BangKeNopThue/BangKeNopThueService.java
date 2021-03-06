package com.adg.api.department.InternationalPayment.disbursement.bank.viettin.writer.BangKeNopThue;

import com.adg.api.department.InternationalPayment.disbursement.NhaCungCapDTO;
import com.adg.api.department.InternationalPayment.disbursement.office.AdgExcelTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.disbursement.office.excel.ExcelTable;
import com.adg.api.department.InternationalPayment.disbursement.office.excel.ExcelUtils;
import com.adg.api.department.InternationalPayment.disbursement.office.excel.ExcelWriter;
import com.adg.api.department.InternationalPayment.disbursement.reader.header.ToKhaiHaiQuanHeaderInfoMetadata;
import com.adg.api.util.MoneyUtils;
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
 * Created on: 2022.05.29 19:13
 */
@Log4j2
public class BangKeNopThueService {

    private final ExcelWriter excelWriter;
    private final ExcelTable excelTable;
    private final Map<String, Object> data;
    private final String outputFolder;
    private final ZonedDateTime fileDate;
    private double totalCost = 0;
    private final String nhaCungCap;
    private final String soToKhai;


    public BangKeNopThueService(String outputFolder, List<Map<String, Object>> toKhaiHaiQuanRecords, String soToKhai, ZonedDateTime fileDate, InputStream inputStream) {
        this.outputFolder = outputFolder;
        this.excelWriter = new ExcelWriter(inputStream);
        this.excelWriter.openSheet();
        this.excelTable = new ExcelTable(this.excelWriter, AdgExcelTableHeaderMetadata.getBangKeNopThue());
        this.fileDate = fileDate;
        this.nhaCungCap = MapUtils.getString(toKhaiHaiQuanRecords.get(0), ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.deAccentedName);
        this.soToKhai = soToKhai;
        this.data = this.transformRecords(toKhaiHaiQuanRecords);
    }

    @SneakyThrows
    public static Map<String, Object> writeOut(String outputFolder, Map<String, Object> toKhaiHaiQuanRecordsGroupBySoToKhai, ZonedDateTime fileDate, Resource resource) {
        long t1 = System.currentTimeMillis();

        if (toKhaiHaiQuanRecordsGroupBySoToKhai.isEmpty()) {
            return MapUtils.ImmutableMap()
                    .put("step", "Generate 'B???ng K?? N???p Thu???'")
                    .put("duration", "0s")
                    .put("detail", List.of())
                    .build();
        }

        List<Map<String, Object>> statsList = new ArrayList<>();
        for (String soToKhai : toKhaiHaiQuanRecordsGroupBySoToKhai.keySet()) {
            Map<String, Object> stats = new BangKeNopThueService(outputFolder, MapUtils.getListMapStringObject(toKhaiHaiQuanRecordsGroupBySoToKhai, soToKhai), soToKhai, fileDate, resource.getInputStream())
                    .exportDocument();
            statsList.add(stats);
        }
        return MapUtils.ImmutableMap()
                .put("step", "Generate 'B???ng K?? N???p Thu???'")
                .put("duration", DateTimeUtils.getRunningTimeInSecond(t1))
                .put("detail", statsList)
                .build();
    }

    private Map<String, Object> transformRecords(List<Map<String, Object>> toKhaiHaiQuanRecords) {
        List<Map<String, Object>> table = new ArrayList<>();
        int stt = 1;
        for (Map<String, Object> toKhaiHaiQuanRecord : toKhaiHaiQuanRecords) {
            Map<String, Object> record = new HashMap<>();
            for (BangKeNopThueHeaderInfoMetadata headerInfo : BangKeNopThueHeaderInfoMetadata.values()) {
                record.put(headerInfo.getHeaderName(), headerInfo.transformToKhaiHaiQuanCallback.apply(toKhaiHaiQuanRecord));
            }

            totalCost += MapUtils.getDouble(toKhaiHaiQuanRecord, ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.deAccentedName);

            List<Map<String, Object>> chiTietThue = MapUtils.getListMapStringObject(toKhaiHaiQuanRecord, ToKhaiHaiQuanHeaderInfoMetadata.ChiTietThue.deAccentedName);
            for (Map<String, Object> map : chiTietThue) {
                Map<String, Object> clonedRecord = new HashMap<>(record);
                String tenSacThue =  MapUtils.getString(map, ToKhaiHaiQuanHeaderInfoMetadata.TenSacThue.deAccentedName);

                clonedRecord.put(BangKeNopThueHeaderInfoMetadata.STT.getHeaderName(), stt);
                clonedRecord.put(BangKeNopThueHeaderInfoMetadata.SoTien.getHeaderName(), MapUtils.getString(map, ToKhaiHaiQuanHeaderInfoMetadata.TienThue.deAccentedName));
                if (tenSacThue.equals("Thu??? NK")) {
                    clonedRecord.put(BangKeNopThueHeaderInfoMetadata.MaNDKTTM.getHeaderName(), "1901");
                    clonedRecord.put(BangKeNopThueHeaderInfoMetadata.NoiDungKhoanNop.getHeaderName(), "Thu??? nh???p kh???u");
                }

                table.add(clonedRecord);

                stt++;
            }
        }
        return MapUtils.ImmutableMap()
                .put("B???ng k??", table)
                .build();
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
                    "Generate 'B???ng K?? N???p Thu???'",
                    MapUtils.getString(stats, "fileName"),
                    MapUtils.getString(stats, "fillTableDuration", "none"),
                    MapUtils.getString(stats, "fillOtherDataDuration"),
                    MapUtils.getString(stats, "writeFileDuration")
            );
        }
        return stats;
    }

    private void insertRecordToTable() {
        List<Map<String, Object>> records = MapUtils.getListMapStringObject(this.data, "B???ng k??");
        records.forEach(item -> this.excelTable.insert(item));
        this.excelTable.removeSampleRow();
    }

    private String build() {
        String fileName = String.format("B???ng k?? n???p thu??? - %s - %s.xlsx",
                this.soToKhai,
                DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_03)
        );
        this.excelWriter.build(outputFolder + "/" + fileName);
        return fileName;
    }

    private void fillData() {
        Cell soTienHeaderCell = this.excelWriter.getCell(BangKeNopThueHeaderInfoMetadata.SoTien.getCellAddress());
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

        Cell kyThueHeaderCell =  this.excelWriter.getCell(BangKeNopThueHeaderInfoMetadata.KyThue.getCellAddress());
        Cell soTienBangChuCell = this.excelWriter.getCell(
                this.excelWriter.getRow(kyThueHeaderCell.getRowIndex() + this.excelTable.getSize() + 2),
                kyThueHeaderCell.getColumnIndex()
        );
        ExcelUtils.setCell(soTienBangChuCell, MoneyUtils.convertMoneyToText(this.totalCost).toLowerCase(),CellType.STRING);

        ExcelUtils.setCell(tongCell, String.format("SUM(%s:%s)", startCell, endCell), CellType.FORMULA);

        NhaCungCapDTO nhaCungCapDTO = NhaCungCapDTO.nhaCungCapMap.get(this.nhaCungCap);
        String nganHang = "xxxx-xxxx-xxxx";
        if (nhaCungCapDTO != null) {
            nganHang = nhaCungCapDTO.getTenNganHang();
        }

//        ExcelUtils.setCell(this.excelWriter.getCell("H19"), nganHang, CellType.STRING);
//        ExcelUtils.setCell(this.excelWriter.getCell("H24"), this.nhaCungCap, CellType.STRING);

        ExcelUtils.setCell(this.excelWriter.getCell("D19"), nganHang, CellType.STRING);
        ExcelUtils.setCell(this.excelWriter.getCell("D24"), this.nhaCungCap, CellType.STRING);
    }

}
