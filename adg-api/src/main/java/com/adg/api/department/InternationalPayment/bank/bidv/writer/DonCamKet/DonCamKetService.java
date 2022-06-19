package com.adg.api.department.InternationalPayment.bank.bidv.writer.DonCamKet;

import com.adg.api.department.InternationalPayment.handler.office.AdgWordTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.handler.office.word.WordWriter;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.03 20:24
 */
@Log4j2
public class DonCamKetService {

    private final WordWriter wordWriter;
    private final String outputFolder;
    private final Map<String, Object> data;
    private final ZonedDateTime fileDate;

    public DonCamKetService(String outputFolder, List<Map<String, Object>> hoaDonRecords, ZonedDateTime fileDate, InputStream templateInputStream) {
        this.wordWriter = new WordWriter(templateInputStream, AdgWordTableHeaderMetadata.getHeaderMapDonCamKet());
        this.outputFolder = outputFolder;
        this.fileDate = fileDate;
        this.data = this.transformHoaDonRecords(hoaDonRecords);
    }

    @SneakyThrows
    public static Map<String, Object> writeOut(String outputFolder,
                                List<Map<String, Object>> hoaDonRecords,
                                ZonedDateTime fileDate,
                                Resource resource
    ) {
        long t1 = System.currentTimeMillis();
        Map<String, Object> stats = new DonCamKetService(outputFolder, hoaDonRecords, fileDate, resource.getInputStream()).exportDocument();
        return MapUtils.ImmutableMap()
                .put("step", "Generate 'Đơn Cam Kết'")
                .put("duration", DateTimeUtils.getRunningTimeInSecond(t1))
                .put("detail", List.of(stats))
                .build();
    }

    private Map<String, Object> transformHoaDonRecords(List<Map<String, Object>> hoaDonRecords) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> table = new ArrayList<>();

        for (Map<String, Object> hoaDonRecord : hoaDonRecords) {
            Map<String, Object> transformedRecord = new HashMap<>();
            for (DonCamKetTableHeaderInfoMetadata headerInfoMetadata : DonCamKetTableHeaderInfoMetadata.values()) {
                transformedRecord.put(headerInfoMetadata.getHeaderName(), headerInfoMetadata.transformCallback.apply(hoaDonRecord));
            }
            table.add(transformedRecord);
        }
        result.put("Danh mục hoá đơn diện tử", table);

        result.put("Ngày giải ngân", DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_09));

        result.put("ngaykydoncamket", String.format("TP Hồ Chí Minh, ngày %s tháng %s năm %s", fileDate.getDayOfMonth(), fileDate.getMonthValue(), fileDate.getYear()));
        return result;
    }

    private Map<String, Object> exportDocument() {
        Map<String, Object> stats = new HashMap<>();
        try {
            long t1 = System.currentTimeMillis();
            this.fillTextData();
            stats.put("fillOtherDataDuration", DateTimeUtils.getRunningTimeInSecond(t1));

            t1 = System.currentTimeMillis();
            this.fillTableData();
            stats.put("fillTableDuration", DateTimeUtils.getRunningTimeInSecond(t1));

            t1 = System.currentTimeMillis();
            String fileName = this.build();
            stats.put("fileName", fileName);
            stats.put("writeFileDuration", DateTimeUtils.getRunningTimeInSecond(t1));

        } finally {

            log.info("Step: {}. File name: {}. Fill table duration: {}. Fill other data duration: {}. Write file duration: {}",
                    "Generate 'Đơn Cam Kết'",
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
        this.wordWriter.fillTableData(MapUtils.getListMapStringObject(this.data, "Danh mục hoá đơn diện tử"));
    }

    private String build() {
        String fileName = String.format("Đơn cam kết - %s.docx",
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
