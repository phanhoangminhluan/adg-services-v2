package com.adg.api.department.InternationalPayment.disbursement.bank.viettin.writer.GiayNhanNo;

import com.adg.api.department.InternationalPayment.disbursement.office.word.WordWriter;
import com.adg.api.department.InternationalPayment.disbursement.reader.header.HoaDonHeaderMetadata;
import com.adg.api.department.InternationalPayment.disbursement.reader.header.ToKhaiHaiQuanHeaderInfoMetadata;
import com.adg.api.util.MoneyUtils;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.NumberUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.29 15:47
 */
@Log4j2
public class GiayNhanNoService {

    private final WordWriter wordWriter;
    private final String outputFolder;
    private final Map<String, Object> data;
    private final ZonedDateTime fileDate;

    public GiayNhanNoService(String outputFolder, List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> toKhaiHaiQuanRecords, ZonedDateTime fileDate, InputStream inputStream) {
        this.wordWriter = new WordWriter(inputStream, new HashMap<>());
        this.outputFolder = outputFolder;
        this.fileDate = fileDate;
        this.data = this.transformRecords(hoaDonRecords, toKhaiHaiQuanRecords);
    }

    @SneakyThrows
    public static Map<String, Object> writeOut(String outputFolder, List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> toKhaiHaiQuanRecords, ZonedDateTime fileDate, Resource resource) {
        long t1 = System.currentTimeMillis();

        if (toKhaiHaiQuanRecords.isEmpty() && hoaDonRecords.isEmpty()) {
            return MapUtils.ImmutableMap()
                    .put("step", "Generate 'Giấy Nhận Nợ'")
                    .put("duration", "0s")
                    .put("detail", List.of())
                    .build();
        }

        Map<String, Object> stats = new GiayNhanNoService(outputFolder, hoaDonRecords, toKhaiHaiQuanRecords, fileDate, resource.getInputStream())
                .exportDocument();

        return MapUtils.ImmutableMap()
                .put("step", "Generate 'Giấy Nhận Nợ'")
                .put("duration", DateTimeUtils.getRunningTimeInSecond(t1))
                .put("detail", List.of(stats))
                .build();
    }

    private Map<String, Object> transformRecords(List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> toKhaiHaiQuanRecords) {
        double soTienBangSo = 0;
        String soTienBangChu = "";

        Optional<Double> optSoTienHoaDonBangSo = hoaDonRecords
                .stream()
                .map(record -> MapUtils.getDouble(record, HoaDonHeaderMetadata.TongTienThanhToan.deAccentedName))
                .reduce(Double::sum);
        soTienBangSo = optSoTienHoaDonBangSo.isPresent() ? optSoTienHoaDonBangSo.get() : 0;

        Optional<Double> optSoTienTkhqBangSo = toKhaiHaiQuanRecords
                .stream()
                .map(record -> MapUtils.getDouble(record, ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.deAccentedName))
                .reduce(Double::sum);
        soTienBangSo += optSoTienTkhqBangSo.isPresent() ? optSoTienTkhqBangSo.get() : 0;

        soTienBangChu = MoneyUtils.convertMoneyToText(soTienBangSo);

        String mucDichSuDungKhoanNo = "";
        if (!hoaDonRecords.isEmpty()) {
            mucDichSuDungKhoanNo = "Thanh toán tiền hàng trong nước.";
        }
        if (!toKhaiHaiQuanRecords.isEmpty()) {
            mucDichSuDungKhoanNo += " Thanh toán tiền thuế GTGT, thuế NK hàng NK.";
        }


        return MapUtils.ImmutableMap()
                .put("tongTienThanhToanBangSo", NumberUtils.formatNumber1(soTienBangSo))
                .put("tongTienThanhToanBangChu", soTienBangChu.toLowerCase())
                .put("mucDichSuDungKhoanNo", mucDichSuDungKhoanNo.trim())
                .put("ngayFileDate", String.format("Ngày %s tháng %s năm %s", fileDate.getDayOfMonth(), fileDate.getMonthValue(), fileDate.getYear()))
                .build();
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
                    "Generate 'Giấy Nhận Nợ'",
                    MapUtils.getString(stats, "fileName"),
                    MapUtils.getString(stats, "fillTableDuration", "none"),
                    MapUtils.getString(stats, "fillOtherDataDuration"),
                    MapUtils.getString(stats, "writeFileDuration")
            );
        }
        return stats;
    }

    private String build() {
        String fileName = String.format("Giấy nhận nợ - %s.docx",
                DateTimeUtils.convertZonedDateTimeToFormat(
                        fileDate,
                        "UTC",
                        DateTimeUtils.FMT_03
                )
        );
        this.wordWriter.build(outputFolder + "/" + fileName);
        return fileName;
    }

}
