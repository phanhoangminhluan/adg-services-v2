package com.adg.api.department.InternationalPayment.disbursement.bank.bidv.writer.BienBanKiemTraSuDungVonVay;

import com.adg.api.department.InternationalPayment.disbursement.office.AdgWordTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.disbursement.office.word.WordUtils;
import com.adg.api.department.InternationalPayment.disbursement.office.word.WordWriter;
import com.adg.api.department.InternationalPayment.disbursement.reader.header.HoaDonHeaderMetadata;
import com.adg.api.util.MoneyUtils;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.NumberUtils;
import com.merlin.asset.core.utils.ParserUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.09 22:25
 */
@Log4j2
public class BienBanKiemTraSuDungVonVayService {

    private final WordWriter wordWriter;
    private final String outputFolder;
    private final Map<String, Object> data;
    private final ZonedDateTime fileDate;
    private final String contractNumber;

    public BienBanKiemTraSuDungVonVayService(String outputFolder, Map<String, Object> hoaDonRecords, ZonedDateTime fileDate, String contractNumber, InputStream inputStream) {
        this.wordWriter = new WordWriter(inputStream, AdgWordTableHeaderMetadata.getHeaderBienBanKiemTraSuDungVonVay());
        this.outputFolder = outputFolder;
        this.fileDate = fileDate;
        this.contractNumber = contractNumber;
        this.data = this.transformHoaDonRecords(hoaDonRecords);
    }

    private Map<String, Object> exportDocument() {
        Map<String, Object> stats = new HashMap<>();
        try {
            long t1 = System.currentTimeMillis();
            this.fillTextData();
            stats.put("fillOtherDataDuration", DateTimeUtils.getRunningTimeInSecond(t1));

            t1 = System.currentTimeMillis();
            this.fillTableData();
            this.fillTableSumData();
            stats.put("fillTableDuration", DateTimeUtils.getRunningTimeInSecond(t1));

            t1 = System.currentTimeMillis();
            String fileName = this.build();
            stats.put("fileName", fileName);
            stats.put("writeFileDuration", DateTimeUtils.getRunningTimeInSecond(t1));
        } finally {
            log.info("Step: {}. File name: {}. Fill table duration: {}. Fill other data duration: {}. Write file duration: {}",
                    "Generate 'Biên Bản Kiểm Tra Sử Dụng Vốn Vay'",
                    MapUtils.getString(stats, "fileName"),
                    MapUtils.getString(stats, "fillTableDuration"),
                    MapUtils.getString(stats, "fillOtherDataDuration"),
                    MapUtils.getString(stats, "writeFileDuration")
            );
        }
        return stats;

    }

    @SneakyThrows
    public static Map<String, Object> writeOut(
            String outputFolder,
            Map<String, Object> hoaDonRecords,
            ZonedDateTime fileDate,
            String contractNumber,
            Resource resource
    ) {
        long t1 = System.currentTimeMillis();
        Map<String, Object> stats = new BienBanKiemTraSuDungVonVayService(outputFolder, hoaDonRecords, fileDate, contractNumber, resource.getInputStream())
                .exportDocument();
        return MapUtils.ImmutableMap()
                .put("step", "Generate 'Biên Bản Kiểm Tra Sử Dụng Vốn Vay'")
                .put("duration", DateTimeUtils.getRunningTimeInSecond(t1))
                .put("detail", List.of(stats))
                .build();
    }

    private Map<String, Object> transformHoaDonRecords(Map<String, Object> hoaDonRecords) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> arr = new ArrayList<>();
        double tongTienVay = 0;
        for (String nhaCungCap : hoaDonRecords.keySet()) {
            Map<String, Object> hoaDonByNcc = MapUtils.getMapStringObject(hoaDonRecords, nhaCungCap);
            Map<String, Object> transformedRecord = new HashMap<>();
            for (BienBanKiemTraSuDungVonVayHeaderInfoMetadata headerInfoMetadata : BienBanKiemTraSuDungVonVayHeaderInfoMetadata.values()) {
                transformedRecord.put(headerInfoMetadata.getHeaderName(), headerInfoMetadata.transformCallback.apply(hoaDonByNcc));
                switch (headerInfoMetadata) {
                    case SoTienVND: {
                        tongTienVay += ParserUtils.toDouble(MapUtils.getString(hoaDonByNcc, HoaDonHeaderMetadata.TongTienThanhToanCacHoaDon.deAccentedName));
                    }
                }
            }
            arr.add(transformedRecord);
        }


        result.put("Ngày hôm nay", String.format("ngày %s tháng %s năm %s", this.fileDate.getDayOfMonth(), this.fileDate.getMonthValue(), this.fileDate.getYear()));
        result.put("ngayGiaiNgan", DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_09));
        result.put("soHopDong", contractNumber);
        result.put("Đại diện khách hàng", "");
        result.put("Đại diện ngân hàng", "");
        result.put("Tổng tiền vay bằng số", NumberUtils.formatNumber1(tongTienVay));
        result.put("Số tiền vay", NumberUtils.formatNumber1(tongTienVay));
        result.put("Tổng tiền vay bằng chữ", MoneyUtils.convertMoneyToText(tongTienVay));
        result.put("Danh sách thanh toán tiền hàng", arr);


        return result;
    }

    private void fillTextData() {
        this.wordWriter.fillTextData(data);
    }
    private void fillTableData() {
        this.wordWriter.fillTableData(MapUtils.getListMapStringObject(data, "Danh sách thanh toán tiền hàng"));

    }

    private void fillTableSumData() {
        XWPFTableRow row = this.wordWriter.getWordTable().getTable().createRow();
        XWPFTableCell sumCell = WordUtils.Table.mergeCell(row, 0, 3);
        sumCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        XWPFRun run = WordUtils.Table.setCell(sumCell, "Tổng");
        run.setFontFamily("Times New Roman");
        run.setBold(true);
        run.setFontSize(11);
        WordUtils.Table.makeCenter(sumCell);

        XWPFTableCell calculatedSell = WordUtils.Table.mergeCell(row, 1, 2);
        calculatedSell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        String val = String.format("%s VND (Bằng chữ: %s.)", MapUtils.getString(data, "Tổng tiền vay bằng số"), MapUtils.getString(data, "Tổng tiền vay bằng chữ"));
        XWPFRun calculatedCellRun = WordUtils.Table.setCell(calculatedSell, val);
        calculatedCellRun.setFontSize(11);
        calculatedCellRun.setFontFamily("Times New Roman");
        WordUtils.Table.makeCenter(calculatedSell);

    }

    private String build() {
        String fileName = String.format("Biên bản kiểm tra sử dụng vốn vay - %s.docx",
                DateTimeUtils.convertZonedDateTimeToFormat(
                        this.fileDate,
                        "UTC",
                        DateTimeUtils.FMT_03
                )
        );
        this.wordWriter.build(outputFolder + "/" + fileName);
        return fileName;
    }
}
