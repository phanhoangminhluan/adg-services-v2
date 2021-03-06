package com.adg.api.department.InternationalPayment.disbursement.bank.bidv.writer.HopDongTinDung;

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
 * Created on: 2022.05.02 23:45
 */
@Log4j2
public class HopDongTinDungService {

    private final WordWriter wordWriter;
    private final String outputFolder;
    private final Map<String, Object> data;
    private final ZonedDateTime fileDate;
    private final String contractNumber;

    public HopDongTinDungService(String outputFolder, Map<String, Object> hoaDonRecords, ZonedDateTime fileDate, String contractNumber, InputStream inputStream) {
        this.wordWriter = new WordWriter(inputStream, AdgWordTableHeaderMetadata.getHeaderMapHopDongTinDung());
        this.outputFolder = outputFolder;
        this.fileDate = fileDate;
        this.contractNumber = contractNumber;
        this.data = this.transformHoaDonRecords(hoaDonRecords);
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

        Map<String, Object> stats = new HopDongTinDungService(outputFolder, hoaDonRecords, fileDate, contractNumber, resource.getInputStream()).exportDocument();

        return MapUtils.ImmutableMap()
                .put("step", "Generate 'H???p ?????ng T??n D???ng'")
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
            for (HopDongTinDungTableHeaderInfoMetadata headerInfoMetadata : HopDongTinDungTableHeaderInfoMetadata.values()) {
                transformedRecord.put(headerInfoMetadata.getHeaderName(), headerInfoMetadata.transformCallback.apply(hoaDonByNcc));
                switch (headerInfoMetadata) {
                    case SoTienVND: {
                        tongTienVay += ParserUtils.toDouble(MapUtils.getString(hoaDonByNcc, HoaDonHeaderMetadata.TongTienThanhToanCacHoaDon.deAccentedName));
                    }
                }
            }
            arr.add(transformedRecord);
        }

        result.put("S??? h???p ?????ng", String.format("01.%s/2021/8088928/H??TD", contractNumber));
        result.put("T???ng ti???n vay", NumberUtils.formatNumber1(tongTienVay));
        result.put("Ng??y k?? h???p ?????ng t??n d???ng", DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.getFormatterWithDefaultValue("dd-MM-yyyy")));
        result.put("Ng??y k??", String.format("TPHCM, ng??y %s th??ng %s n??m %s", this.fileDate.getDayOfMonth(), this.fileDate.getMonthValue(), this.fileDate.getYear()));
        result.put("T???ng ti???n vay b???ng ch???", MoneyUtils.convertMoneyToText(tongTienVay));

        result.put("N???i dung thanh to??n", arr);

        return result;
    }
    public Map<String, Object> exportDocument() {
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
                    "Generate 'H???p ?????ng T??n D???ng'",
                    MapUtils.getString(stats, "fileName"),
                    MapUtils.getString(stats, "fillTableDuration"),
                    MapUtils.getString(stats, "fillOtherDataDuration"),
                    MapUtils.getString(stats, "writeFileDuration")
            );
        }
        return stats;
    }

    private void fillTextData() {
        this.wordWriter.fillTextData(data);
    }
    private void fillTableData() {
        this.wordWriter.fillTableData(MapUtils.getListMapStringObject(data, "N???i dung thanh to??n"));

    }

    private void fillTableSumData() {
        XWPFTableRow row = this.wordWriter.getWordTable().getTable().createRow();

        XWPFTableCell sumCell = WordUtils.Table.mergeCell(row, 0, 3);
        sumCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        XWPFRun run = WordUtils.Table.setCell(sumCell, "T???ng");
        run.setBold(true);
        run.setFontSize(11);
        WordUtils.Table.makeCenter(sumCell);

        XWPFTableCell calculatedSell = row.getCell(1);
        calculatedSell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        WordUtils.Table.setCell(calculatedSell, MapUtils.getString(data, "T???ng ti???n vay")).setFontSize(11);
        WordUtils.Table.makeCenter(calculatedSell);

    }

    private String build() {
        String fileName = String.format("H???p ?????ng t??n d???ng - %s.docx",
                DateTimeUtils.convertZonedDateTimeToFormat(
                        this.fileDate,
                        "Asia/Ho_Chi_Minh",
                        DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_03)
                )
        );
        this.wordWriter.build(outputFolder + "/" + fileName);
        return fileName;
    }
}
