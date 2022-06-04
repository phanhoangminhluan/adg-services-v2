package com.adg.api.department.InternationalPayment.service.viettin.writer.UyNhiemChi;

import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelUtils;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelWriter;
import com.adg.api.department.InternationalPayment.service.bidv.NhaCungCapDTO;
import com.adg.api.department.InternationalPayment.service.bidv.enums.HoaDonHeaderMetadata;
import com.adg.api.department.InternationalPayment.service.bidv.reader.HoaDonService;
import com.adg.api.util.MoneyUtils;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.29 15:04
 */
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
        public static final String SO_TAI_KHOAN = "C12";
        public static final String NGAN_HANG = "E12";
        public static final String SO_TIEN_BANG_SO = "C15";
        public static final String SO_TIEN_BANG_CHU = "C17";
        public static final String NOI_DUNG = "D18";
    }

    @SneakyThrows
    public static void writeOut(String outputFolder, Map<String, Object> hoaDonRecordsGroupByNhaCungCap, ZonedDateTime fileDate, Resource resource) {
        for (String nhaCungCap : hoaDonRecordsGroupByNhaCungCap.keySet()) {
            new UyNhiemChiService(outputFolder, MapUtils.getListMapStringObject(hoaDonRecordsGroupByNhaCungCap, nhaCungCap), nhaCungCap, fileDate, resource.getInputStream())
                    .exportDocument();
        }
    }

    private void exportDocument() {
        this.fillTextData();
        this.build();
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
                this.excelWriter.getCell(UyNhiemChiAddress.SO_TAI_KHOAN),
                MapUtils.getString(data, "stk"),
                CellType.STRING
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(UyNhiemChiAddress.NGAN_HANG),
                MapUtils.getString(data, "nganHang"),
                CellType.STRING
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(UyNhiemChiAddress.SO_TIEN_BANG_SO),
                MapUtils.getString(data, "soTienBangSo"),
                CellType.NUMERIC
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(UyNhiemChiAddress.SO_TIEN_BANG_CHU),
                MapUtils.getString(data, "soTienBangChu"),
                CellType.STRING
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(UyNhiemChiAddress.NOI_DUNG),
                MapUtils.getString(data, "noiDungThanhToan"),
                CellType.STRING
        );
    }

    private void build() {
        String fileName = String.format("Uỷ nhiệm chi - %s - %s.xlsx",
                this.nhaCungCap,
                DateTimeUtils.convertZonedDateTimeToFormat(
                        fileDate,
                        "UTC",
                        DateTimeUtils.FMT_03
                )
        );
        this.excelWriter.build(this.outputFolder + "/" + fileName);
    }

}
