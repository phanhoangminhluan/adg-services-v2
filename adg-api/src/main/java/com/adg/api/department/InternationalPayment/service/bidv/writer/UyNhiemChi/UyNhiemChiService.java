package com.adg.api.department.InternationalPayment.service.bidv.writer.UyNhiemChi;

import com.adg.api.department.InternationalPayment.handler.office.word.WordWriter;
import com.adg.api.department.InternationalPayment.service.bidv.NhaCungCapDTO;
import com.adg.api.department.InternationalPayment.service.bidv.enums.HoaDonHeaderMetadata;
import com.adg.api.util.MoneyUtils;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.NumberUtils;
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
 * Created on: 2022.05.03 15:33
 */
@Log4j2
public class UyNhiemChiService {

    private final WordWriter wordWriter;
    private final String outputFolder;
    private final Map<String, Object> data;
    private final ZonedDateTime fileDate;

    public UyNhiemChiService(String outputFolder, Map<String, Object> hoaDonRecords, ZonedDateTime fileDate, InputStream inputStream) {
        this.wordWriter = new WordWriter(inputStream, new HashMap<>());
        this.outputFolder = outputFolder;
        this.fileDate = fileDate;
        this.data = this.transformHoaDonRecords(hoaDonRecords);
    }

    @SneakyThrows
    public static Map<String, Object> writeOut(String outputFolder, Map<String, Object> hoaDonRecordsGroupByNhaCungCap, ZonedDateTime fileDate, Resource resource) {
        long t1 = System.currentTimeMillis();
        List<Map<String, Object>> statsList = new ArrayList<>();
        for (String ncc : hoaDonRecordsGroupByNhaCungCap.keySet()) {
            Map<String, Object> summarizedHoaDonByNhaCungCap = MapUtils.getMapStringObject(hoaDonRecordsGroupByNhaCungCap, ncc);
            Map<String, Object> stats = new UyNhiemChiService(outputFolder, summarizedHoaDonByNhaCungCap, fileDate, resource.getInputStream())
                    .exportDocument();
            statsList.add(stats);
        }

        return MapUtils.ImmutableMap()
                .put("step", "Generate 'Uỷ Nhiệm Chi'")
                .put("duration", DateTimeUtils.getRunningTimeInSecond(t1))
                .put("detail", statsList)
                .build();
    }

    public Map<String, Object> transformHoaDonRecords(Map<String, Object> hoaDonRecords) {
        Map<String, Object> result = new HashMap<>();
        String ncc =  MapUtils.getString(hoaDonRecords, HoaDonHeaderMetadata.NhaCungCap.deAccentedName);
        String shortNameNcc = NhaCungCapDTO.nhaCungCapMap.get(ncc) == null ? "xxx-" + System.currentTimeMillis() : NhaCungCapDTO.nhaCungCapMap.get(ncc).getShortName();
        NhaCungCapDTO nhaCungCapDTO = NhaCungCapDTO.nhaCungCapMap.get(MapUtils.getString(hoaDonRecords, HoaDonHeaderMetadata.NhaCungCap.deAccentedName));
        result.put("Người cung cấp", ncc);
        result.put("shortNameNcc", shortNameNcc);
        result.put("Số tiền bằng số", NumberUtils.formatNumber1(MapUtils.getDouble(hoaDonRecords, HoaDonHeaderMetadata.TongTienThanhToanCacHoaDon.deAccentedName)));
        result.put("Số tài khoản", nhaCungCapDTO == null ?  "" : nhaCungCapDTO.getSoTaiKhoan());
        result.put("Ngân hàng", nhaCungCapDTO == null ?  "" : nhaCungCapDTO.getTenNganHang());
        result.put("Số tiền bằng chữ", MoneyUtils.convertMoneyToText(MapUtils.getDouble(hoaDonRecords, HoaDonHeaderMetadata.TongTienThanhToanCacHoaDon.deAccentedName)));
        result.put("Ngày", DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "Asia/Ho_Chi_Minh", DateTimeUtils.getFormatterWithDefaultValue("dd/MM/yyyy")));

        return result;
    }

    private Map<String, Object> exportDocument() {
        Map<String, Object> stats = new HashMap<>();
        try {
            long t1 = System.currentTimeMillis();
            this.wordWriter.fillTextData(data);
            stats.put("fillOtherDataDuration", DateTimeUtils.getRunningTimeInSecond(t1));

            t1 = System.currentTimeMillis();
            String fileName = this.build();
            stats.put("fileName", fileName);
            stats.put("writeFileDuration", DateTimeUtils.getRunningTimeInSecond(t1));
        } finally {
            log.info("Step: {}. File name: {}. Fill table duration: {}. Fill other data duration: {}. Write file duration: {}",
                    "Generate 'Uỷ Nhiệm Chi'",
                    MapUtils.getString(stats, "fileName"),
                    MapUtils.getString(stats, "fillTableDuration", "none"),
                    MapUtils.getString(stats, "fillOtherDataDuration"),
                    MapUtils.getString(stats, "writeFileDuration")
            );
        }

        return stats;
    }

    private String build() {
        String fileName = String.format("Uỷ nhiệm chi - %s - %s.docx",
                MapUtils.getString(this.data, "shortNameNcc"),
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
