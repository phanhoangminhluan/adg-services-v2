package com.adg.api.department.InternationalPayment.service.bidv.writer.DonCamKet;

import com.adg.api.department.InternationalPayment.handler.office.AdgWordTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.handler.office.word.WordWriter;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;

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

    public DonCamKetService(String outputFolder, List<Map<String, Object>> hoaDonRecords, InputStream inputStream) {
        this.wordWriter = new WordWriter(inputStream, AdgWordTableHeaderMetadata.getHeaderMapDonCamKet());
        this.outputFolder = outputFolder;
        this.data = this.transformHoaDonRecords(hoaDonRecords);
    }


    public Map<String, Object> transformHoaDonRecords(List<Map<String, Object>> hoaDonRecords) {
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

        result.put("Ngày giải ngân", DateTimeUtils.convertZonedDateTimeToFormat(ZonedDateTime.now(), "Asia/Ho_Chi_Minh", DateTimeUtils.FMT_10));
        result.put("ngaykydoncamket", String.format("TP Hồ Chí Minh, ngày %s tháng %s năm %s", ZonedDateTime.now().getDayOfMonth(), ZonedDateTime.now().getMonthValue(), ZonedDateTime.now().getYear()));
        return result;
    }

    public void exportDocument() {
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
                        ZonedDateTime.now(),
                        "Asia/Ho_Chi_Minh",
                        DateTimeUtils.FMT_03
                )
        );
        this.wordWriter.build(outputFolder + "/" + fileName);
    }
}
