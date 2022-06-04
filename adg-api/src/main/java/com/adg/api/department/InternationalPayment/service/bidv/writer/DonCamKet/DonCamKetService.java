package com.adg.api.department.InternationalPayment.service.bidv.writer.DonCamKet;

import com.adg.api.department.InternationalPayment.handler.office.AdgWordTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.handler.office.word.WordWriter;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
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
    public static void writeOut(String outputFolder,
                                List<Map<String, Object>> hoaDonRecords,
                                ZonedDateTime fileDate,
                                Resource resource
    ) {
        new DonCamKetService(outputFolder, hoaDonRecords, fileDate, resource.getInputStream()).exportDocument();
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

    private void exportDocument() {
        this.fillTextData();
        this.fillTableData();
        this.build();
    }

    private void fillTextData() {
        this.wordWriter.fillTextData(data);
    }

    private void fillTableData() {
        this.wordWriter.fillTableData(MapUtils.getListMapStringObject(this.data, "Danh mục hoá đơn diện tử"));
    }

    private void build() {
        String fileName = String.format("Đơn cam kết - %s.docx",
                DateTimeUtils.convertZonedDateTimeToFormat(
                        this.fileDate,
                        "UTC",
                        DateTimeUtils.FMT_03
                )
        );
        this.wordWriter.build(outputFolder + "/" + fileName);
    }
}
