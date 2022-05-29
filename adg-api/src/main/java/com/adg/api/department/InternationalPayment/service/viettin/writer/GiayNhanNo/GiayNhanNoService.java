package com.adg.api.department.InternationalPayment.service.viettin.writer.GiayNhanNo;

import com.adg.api.department.InternationalPayment.handler.office.word.WordWriter;
import com.adg.api.department.InternationalPayment.service.bidv.enums.HoaDonHeaderMetadata;
import com.adg.api.department.InternationalPayment.service.viettin.reader.ToKhaiHaiQuanHeaderInfoMetadata;
import com.adg.api.util.MoneyUtils;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.NumberUtils;

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

        return MapUtils.ImmutableMap()
                .put("tongTienThanhToanBangSo", NumberUtils.formatNumber1(soTienBangSo))
                .put("tongTienThanhToanBangChu", soTienBangChu)
                .put("ngayFileDate", String.format("Ngày %s tháng %s năm %s", fileDate.getDayOfMonth(), fileDate.getMonthValue(), fileDate.getYear()))
                .build();
    }

    public void exportDocument() {
        this.wordWriter.fillTextData(data);
        this.build();
    }

    private void build() {
        String fileName = String.format("Giấy nhận nợ - %s.docx",
                DateTimeUtils.convertZonedDateTimeToFormat(
                        fileDate,
                        "UTC",
                        DateTimeUtils.FMT_03
                )
        );
        this.wordWriter.build(outputFolder + "/" + fileName);
    }

}
