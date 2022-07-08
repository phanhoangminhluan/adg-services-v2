package com.adg.api.department.InternationalPayment.disbursement.bank.viettin.writer.UyNhiemChi;

import com.adg.api.department.InternationalPayment.disbursement.NhaCungCapDTO;
import com.adg.api.department.InternationalPayment.disbursement.office.excel.ExcelUtils;
import com.adg.api.department.InternationalPayment.disbursement.office.excel.ExcelWriter;
import com.adg.api.department.InternationalPayment.disbursement.reader.header.HoaDonHeaderMetadata;
import com.adg.api.department.InternationalPayment.disbursement.reader.service.HoaDonService;
import com.adg.api.util.MoneyUtils;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.29 15:04
 */
@Log4j2
public class UyNhiemChiService {

    private ExcelWriter excelWriter;
    private Map<String, Object> data;
    private String outputFolder;
    private ZonedDateTime fileDate;
    private String nhaCungCap;

    public UyNhiemChiService(String outputFolder, List<Map<String, Object>> hoaDonRecords, String nhaCungCap, ZonedDateTime fileDate, InputStream inputStream) {
        this.outputFolder = outputFolder;
        this.excelWriter = new ExcelWriter(inputStream);
        this.excelWriter.openSheet();
        this.fileDate = fileDate;
        this.nhaCungCap = nhaCungCap;
        this.data = this.transformHoaDonRecords(hoaDonRecords);
    }

    private static class UyNhiemChiAddress {

        public static class Copy1 {
            public static final String SO_TAI_KHOAN = "C12";
            public static final String NGAN_HANG = "E12";
            public static final String SO_TIEN_BANG_SO = "C15";
            public static final String SO_TIEN_BANG_CHU = "C17";
            public static final String NOI_DUNG = "D18";
        }

        public static class Copy2 {
            public static final String SO_TAI_KHOAN = "C38";
            public static final String NGAN_HANG = "E38";
            public static final String SO_TIEN_BANG_SO = "C41";
            public static final String SO_TIEN_BANG_CHU = "C43";
            public static final String NOI_DUNG = "D44";
        }

    }

    @SneakyThrows
    public static Map<String, Object> writeOut(String outputFolder, Map<String, Object> hoaDonRecordsGroupByNhaCungCap, ZonedDateTime fileDate, Resource resource) {
        long t1 = System.currentTimeMillis();
        List<Map<String, Object>> statsList = new ArrayList<>();

        if (hoaDonRecordsGroupByNhaCungCap.isEmpty()) {
            return MapUtils.ImmutableMap()
                    .put("step", "Generate 'Uỷ Nhiệm Chi'")
                    .put("duration", "0s")
                    .put("detail", List.of())
                    .build();
        }

        for (String nhaCungCap : hoaDonRecordsGroupByNhaCungCap.keySet()) {
            Map<String, Object> stats = new UyNhiemChiService(outputFolder, MapUtils.getListMapStringObject(hoaDonRecordsGroupByNhaCungCap, nhaCungCap), nhaCungCap, fileDate, resource.getInputStream())
                    .exportDocument();
            statsList.add(stats);
        }
        return MapUtils.ImmutableMap()
                .put("step", "Generate 'Uỷ Nhiệm Chi'")
                .put("duration", DateTimeUtils.getRunningTimeInSecond(t1))
                .put("detail", statsList)
                .build();
    }

    private Map<String, Object> exportDocument() {
        Map<String, Object> stats = new HashMap<>();

        try {
            long t1 = System.currentTimeMillis();
            this.fillTextData();
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

    private Map<String, Object> transformHoaDonRecords(List<Map<String, Object>> hoaDonRecords) {
        String nganHang = "xxxx-xxxx-xxxx";
        String stk = "xxxx-xxxx-xxxx";
        String noiDungThanhToan = "";
        double soTienBangSo = 0;
        String soTienBangChu = "";

        NhaCungCapDTO nhaCungCapDTO = NhaCungCapDTO.nhaCungCapMap.get(nhaCungCap);
        if (nhaCungCapDTO != null) {
            nganHang = nhaCungCapDTO.getTenNganHang();
            stk = nhaCungCapDTO.getSoTaiKhoan();
        }
        noiDungThanhToan = hoaDonRecords
                .stream()
                .map(record -> MapUtils.getString(record, HoaDonHeaderMetadata.SoHoaDon.deAccentedName))
                .map(HoaDonService::transformSoHoaDon)
                .collect(Collectors.joining(", "));
        Optional<Double> optSoTienBangSo = hoaDonRecords
                .stream()
                .map(record -> MapUtils.getDouble(record, HoaDonHeaderMetadata.TongTienThanhToan.deAccentedName))
                .reduce(Double::sum);
        soTienBangSo = optSoTienBangSo.isPresent() ? optSoTienBangSo.get() : 0;
        soTienBangChu = MoneyUtils.convertMoneyToText(soTienBangSo);

        return MapUtils.ImmutableMap()
                .put("nganHang", nganHang)
                .put("stk", stk)
                .put("noiDungThanhToan", noiDungThanhToan)
                .put("soTienBangSo", soTienBangSo)
                .put("soTienBangChu", soTienBangChu)
                .build();
    }

    private void fillTextData() {
        ExcelUtils.setCell(
                this.excelWriter.getCell(UyNhiemChiAddress.Copy1.SO_TAI_KHOAN),
                MapUtils.getString(data, "stk"),
                CellType.STRING
        );
        ExcelUtils.setCell(
                this.excelWriter.getCell(UyNhiemChiAddress.Copy2.SO_TAI_KHOAN),
                MapUtils.getString(data, "stk"),
                CellType.STRING
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(UyNhiemChiAddress.Copy1.NGAN_HANG),
                MapUtils.getString(data, "nganHang"),
                CellType.STRING
        );
        ExcelUtils.setCell(
                this.excelWriter.getCell(UyNhiemChiAddress.Copy2.NGAN_HANG),
                MapUtils.getString(data, "nganHang"),
                CellType.STRING
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(UyNhiemChiAddress.Copy1.SO_TIEN_BANG_SO),
                MapUtils.getString(data, "soTienBangSo"),
                CellType.NUMERIC
        );
        ExcelUtils.setCell(
                this.excelWriter.getCell(UyNhiemChiAddress.Copy2.SO_TIEN_BANG_SO),
                MapUtils.getString(data, "soTienBangSo"),
                CellType.NUMERIC
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(UyNhiemChiAddress.Copy1.SO_TIEN_BANG_CHU),
                MapUtils.getString(data, "soTienBangChu"),
                CellType.STRING
        );
        ExcelUtils.setCell(
                this.excelWriter.getCell(UyNhiemChiAddress.Copy2.SO_TIEN_BANG_CHU),
                MapUtils.getString(data, "soTienBangChu"),
                CellType.STRING
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(UyNhiemChiAddress.Copy1.NOI_DUNG),
                MapUtils.getString(data, "noiDungThanhToan"),
                CellType.STRING
        );
        ExcelUtils.setCell(
                this.excelWriter.getCell(UyNhiemChiAddress.Copy2.NOI_DUNG),
                MapUtils.getString(data, "noiDungThanhToan"),
                CellType.STRING
        );
    }

    private String build() {

        String shortNameNcc = NhaCungCapDTO.nhaCungCapMap.get(this.nhaCungCap) == null ? "xxx-" + System.currentTimeMillis() : NhaCungCapDTO.nhaCungCapMap.get(this.nhaCungCap).getShortName();

        String fileName = String.format("Uỷ nhiệm chi - %s - %s.xlsx",
                shortNameNcc,
                DateTimeUtils.convertZonedDateTimeToFormat(
                        fileDate,
                        "UTC",
                        DateTimeUtils.FMT_03
                )
        );
        this.excelWriter.build(this.outputFolder + "/" + fileName);
        return fileName;
    }

}
