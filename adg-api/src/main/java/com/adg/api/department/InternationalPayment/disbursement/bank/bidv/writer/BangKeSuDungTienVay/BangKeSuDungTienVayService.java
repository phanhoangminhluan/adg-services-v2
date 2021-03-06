package com.adg.api.department.InternationalPayment.disbursement.bank.bidv.writer.BangKeSuDungTienVay;

import com.adg.api.department.InternationalPayment.disbursement.office.AdgExcelTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.disbursement.office.excel.ExcelTable;
import com.adg.api.department.InternationalPayment.disbursement.office.excel.ExcelUtils;
import com.adg.api.department.InternationalPayment.disbursement.office.excel.ExcelWriter;
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
 * Created on: 2022.04.30 13:28
 */
@Log4j2
public class BangKeSuDungTienVayService {

    private final ExcelWriter excelWriter;
    private final ExcelTable excelTable;
    private final Map<String, Object> data;
    private final String outputFolder;
    private final ZonedDateTime fileDate;
    private final String contractNumber;


    public BangKeSuDungTienVayService(String outputFolder, List<Map<String, Object>> hoaDonRecords, ZonedDateTime fileDate, String contractNumber, InputStream templateInputStream) {
        this.outputFolder = outputFolder;
        this.excelWriter = new ExcelWriter(templateInputStream);
        this.excelWriter.openSheet();
        this.excelTable = new ExcelTable(this.excelWriter, AdgExcelTableHeaderMetadata.getBidvBangKeSuDungTienVay());
        this.fileDate = fileDate;
        this.contractNumber = contractNumber;
        this.data = this.transformHoaDonRecords(hoaDonRecords);
    }

    @SneakyThrows
    public static Map<String, Object> writeOut(
            String outputFolder,
            List<Map<String, Object>> hoaDonRecords,
            ZonedDateTime fileDate,
            String contractNumber,
            Resource template
    ) {
        long t1 = System.currentTimeMillis();
        Map<String, Object> stats = new BangKeSuDungTienVayService(outputFolder, hoaDonRecords, fileDate, contractNumber, template.getInputStream())
                .exportDocument();
        return MapUtils.ImmutableMap()
                .put("step", "Generate 'B???ng K?? S??? D???ng Ti???n Vay'")
                .put("duration", DateTimeUtils.getRunningTimeInSecond(t1))
                .put("detail", List.of(stats))
                .build();

    }

    private Map<String, Object> transformHoaDonRecords(List<Map<String, Object>> hoaDonRecords) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> bangKe = new ArrayList<>();

        for (Map<String, Object> hoaDonRecord : hoaDonRecords) {
            Map<String, Object> transformedRecord = new HashMap<>();
            for (BangKeSuDungTienVayHeaderInfoMetadata headerInfoMetadata : BangKeSuDungTienVayHeaderInfoMetadata.values()) {
                transformedRecord.put(headerInfoMetadata.getHeaderName(), headerInfoMetadata.transformCallback.apply(hoaDonRecord));
            }
            bangKe.add(transformedRecord);
        }
        result.put("B???ng k??", bangKe);
        return result;
    }

    private void insertRecordToTable() {
        List<Map<String, Object>> records = MapUtils.getListMapStringObject(this.data, "B???ng k??");
        records.forEach(item -> this.excelTable.insert(item));
    }

    private Map<String, Object> exportDocument() {
        Map<String, Object> stats = new HashMap<>();
        try {
            long t1 = System.currentTimeMillis();
            this.insertRecordToTable();
            this.excelTable.removeSampleRow();
            stats.put("fillTableDuration", DateTimeUtils.getRunningTimeInSecond(t1));

            t1 = System.currentTimeMillis();
            this.fillSum();
            stats.put("fillOtherDataDuration", DateTimeUtils.getRunningTimeInSecond(t1));

            t1 = System.currentTimeMillis();
            String fileName = this.build();
            stats.put("fileName", fileName);
            stats.put("writeFileDuration", DateTimeUtils.getRunningTimeInSecond(t1));
        } finally {
            log.info("Step: {}. File name: {}. Fill table duration: {}. Fill other data duration: {}. Write file duration: {}",
                    "Generate 'B???ng K?? S??? D???ng Ti???n Vay'",
                    MapUtils.getString(stats, "fileName"),
                    MapUtils.getString(stats, "fillTableDuration"),
                    MapUtils.getString(stats, "fillOtherDataDuration"),
                    MapUtils.getString(stats, "writeFileDuration")
            );
        }
        return stats;
    }

    private String build() {
        String fileName = String.format("B???ng k?? s??? d???ng ti???n vay - %s.xlsx", DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_03));
        this.excelWriter.build(outputFolder + "/" + fileName);
        return fileName;
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
                String.format("Chi ti???t n???i dung s??? d???ng ti???n vay theo h???p ?????ng t??n d???ng ng???n h???n c??? th??? s??? : 01.%s/2021/8088928/H??TD ng??y %s ???????c k?? k???t gi???a Ng??n h??ng v?? B??n vay.", contractNumber, DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_09)),
                CellType.STRING);

        Cell sttHeaderCell = this.excelWriter.getCell(BangKeSuDungTienVayHeaderInfoMetadata.TT.getCellAddress());

        Cell finalCell = this.excelWriter.getCell(
                this.excelWriter.getRow(sttHeaderCell.getRowIndex() + this.excelTable.getSize() + 3),
                sttHeaderCell.getColumnIndex()
        );

        ExcelUtils.setCell(
                finalCell,
                String.format("B???ng k?? n??y l?? m???t b??? ph???n trong th??? t??ch r???i h???p ?????ng t??n d???ng ng???n h???n c??? th??? s??? 01.%s/2021/8088928/H??TD ng??y %s ???????c k?? k???t gi???a Ng??n h??ng v?? B??n vay.", contractNumber, DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_09)),
                CellType.STRING);
    }

}
