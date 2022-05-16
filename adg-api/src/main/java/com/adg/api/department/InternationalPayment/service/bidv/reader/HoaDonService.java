package com.adg.api.department.InternationalPayment.service.bidv.reader;

import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelReader;
import com.adg.api.department.InternationalPayment.service.bidv.enums.HoaDonHeaderMetadata;
import com.adg.api.department.InternationalPayment.service.bidv.enums.PhieuNhapKhoHeaderMetadata;
import com.adg.api.department.InternationalPayment.service.bidv.writer.BangKeSuDungTienVay.BangKeSuDungTienVayService;
import com.adg.api.department.InternationalPayment.service.bidv.writer.BienBanKiemTraSuDungVonVay.BienBanKiemTraSuDungVonVayService;
import com.adg.api.department.InternationalPayment.service.bidv.writer.DonCamKet.DonCamKetService;
import com.adg.api.department.InternationalPayment.service.bidv.writer.DonMuaHang.DonMuaHangService;
import com.adg.api.department.InternationalPayment.service.bidv.writer.HopDongTinDung.HopDongTinDungService;
import com.adg.api.department.InternationalPayment.service.bidv.writer.UyNhiemChi.UyNhiemChiService;
import com.adg.api.util.ZipUtils;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.ParserUtils;
import com.merlin.asset.core.utils.StringUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.10 21:47
 */
@Service
public class HoaDonService {

    private static final Logger logger = LoggerFactory.getLogger(HoaDonService.class);

    @Value("${international-payment.bidv.output.files}")
    private String output;

    @Value("${international-payment.bidv.output.zip}")
    private String outputZipFolder;

    @Value("${international-payment.bidv.output.template.bang-ke-su-dung-tien-vay}")
    private String bangKeSuDungTienVayTemplate;

    @Value("${international-payment.bidv.output.template.bien-ban-kiem-tra-su-dung-von-vay}")
    private String bienBanKiemTraSuDungVonVayTemplate;

    @Value("${international-payment.bidv.output.template.don-cam-ket}")
    private String donCamKetTemplate;

    @Value("${international-payment.bidv.output.template.don-mua-hang}")
    private String donMuaHangTemplate;

    @Value("${international-payment.bidv.output.template.hop-dong-tin-dung}")
    private String hopDongTinDungTemplate;

    @Value("${international-payment.bidv.output.template.uy-nhiem-chi}")
    private String uyNhiemChiTemplate;



    private String getOutputFolder() {
        String path = String.format(output, DateTimeUtils.convertZonedDateTimeToFormat(ZonedDateTime.now(), "Asia/Ho_Chi_Minh", DateTimeUtils.getFormatterWithDefaultValue("yyyy/MM/dd/HHmmss")));
        File file = new File(path);
        file.mkdirs();
        return path;
    }

    private String getOutputZipFolder() {
        String path = String.format(outputZipFolder, DateTimeUtils.convertZonedDateTimeToFormat(ZonedDateTime.now(), "Asia/Ho_Chi_Minh", DateTimeUtils.getFormatterWithDefaultValue("yyyy/MM/dd/HHmmss")));
        File file = new File(path);
        file.mkdirs();
        return path;
    }

    public List<Map<String, Object>> readHoaDonTable(String fileHoaDonPath) {
        ExcelReader excelReader = new ExcelReader(fileHoaDonPath);
        Map<String, Object> output = excelReader.readTable("A2");
        List<Map<String, Object>> records = MapUtils.getListMapStringObject(output, "records");
        List<Map<String, Object>> actualRecords = records.stream().filter(record -> !ParserUtils.isNullOrEmpty(MapUtils.getString(record, HoaDonHeaderMetadata.SoChungTu.name, "").trim())).collect(Collectors.toList());

        this.validate(MapUtils.ImmutableMap()
                .put("headers", MapUtils.getListMapStringObject(output, "headers"))
                .put("records", actualRecords)
                .build());

        List<Map<String, Object>> deAccentedRecords = new ArrayList<>();
        for (Map<String, Object> record : actualRecords) {
            deAccentedRecords.add(this.deAccentAllKeys(record));
        }

        return deAccentedRecords;
    }

    public Map<String, Object> readPhieuNhapKho(List<String> listFilePhieuNhapKho) {
        Map<String, Object> phieuNhapKhoByNCC = new HashMap<>();
        for (String filePhieuNhapKho : listFilePhieuNhapKho) {
            ExcelReader excelReader = new ExcelReader(filePhieuNhapKho);
            Map<String, Object> output = excelReader.readTable("A16");
            List<Map<String, Object>> records = MapUtils.getListMapStringObject(output, "records");
            String description = excelReader.getCellValueAsString("A10");
            Map<String, Object> phieuNhapKhoDescription = this.parsePhieuNhapKhoDescription(description);

            String ncc = MapUtils.getString(phieuNhapKhoDescription, PhieuNhapKhoHeaderMetadata.NhaCungCap.deAccentedName);
            String soHoaDon = MapUtils.getString(phieuNhapKhoDescription, PhieuNhapKhoHeaderMetadata.SoHoaDon.deAccentedName);
            Map<String, Object> donHangCuaNCC = MapUtils.getMapStringObject(phieuNhapKhoByNCC, ncc, new HashMap<>());
            List<Map<String, Object>> actualRecords = new ArrayList<>();
            for (Map<String, Object> record : records) {
                if (ParserUtils.isNullOrEmpty(MapUtils.getString(record, PhieuNhapKhoHeaderMetadata.STT.name))) {
                    continue;
                }
                if (MapUtils.getString(record, PhieuNhapKhoHeaderMetadata.STT.name).equals("- Tổng số tiền (Viết bằng chữ):")) {
                    break;
                }
                Map<String, Object> actualRecord = new HashMap<>();
                for (PhieuNhapKhoHeaderMetadata headerMetadata : PhieuNhapKhoHeaderMetadata.values()) {
                    if (!headerMetadata.isOriginalField) {
                        continue;
                    }
                    actualRecord.put(headerMetadata.deAccentedName, MapUtils.getString(record, headerMetadata.name));
                }
                actualRecord.put(PhieuNhapKhoHeaderMetadata.NhaCungCap.deAccentedName, ncc);
                actualRecord.put(PhieuNhapKhoHeaderMetadata.SoHoaDon.deAccentedName, soHoaDon);
                actualRecords.add(actualRecord);
            }
            donHangCuaNCC.put(soHoaDon, actualRecords);
            phieuNhapKhoByNCC.put(ncc, donHangCuaNCC);
        }


        return phieuNhapKhoByNCC;
    }

    public List<Map<String, Object>> convertPnkToDTO(Map<String, Object> mapPnk) {
        List<Map<String, Object>> listPnk = new ArrayList<>();

        for (String nhaCungCap : mapPnk.keySet()) {
            Map<String, Object> hoaDonCuaNcc = MapUtils.getMapStringObject(mapPnk, nhaCungCap);
            for (String soHoaDon : hoaDonCuaNcc.keySet()) {
                List<Map<String, Object>> listSanPham = MapUtils.getListMapStringObject(hoaDonCuaNcc, soHoaDon);
                listPnk.addAll(listSanPham);
            }
        }
        return listPnk;
    }

    public Map<String, Object> convertDtoToPnk(List<Map<String, Object>> listPnk) {
        Map<String, Object> mapPnk = new HashMap<>();

        for (Map<String, Object> pnk : listPnk) {
            String ncc = MapUtils.getString(pnk, PhieuNhapKhoHeaderMetadata.NhaCungCap.deAccentedName);
            String soHoaDon = MapUtils.getString(pnk, PhieuNhapKhoHeaderMetadata.SoHoaDon.deAccentedName);

            Map<String, Object> hoaDonCuaNcc = MapUtils.getMapStringObject(pnk, ncc, new HashMap<>());

            List<Map<String, Object>> sanPhamCuaHoaDon = MapUtils.getListMapStringObject(hoaDonCuaNcc, soHoaDon, new ArrayList<>());
            sanPhamCuaHoaDon.add(pnk);

            hoaDonCuaNcc.put(soHoaDon, sanPhamCuaHoaDon);
            mapPnk.put(ncc,hoaDonCuaNcc );
        }
        return mapPnk;
    }

    private Map<String, Object> parsePhieuNhapKhoDescription(String description) {
        Map<String, Object> output = new HashMap<>();
        List<String> arr = Arrays.asList(description.split("của"));
        String ncc = arr.get(1).trim();
        String firstPart = arr.get(0).replace("- Theo hóa đơn số", "").trim();
        List<String> arr2 = Arrays.asList(firstPart.split(" ")).stream().filter(str -> (
                !str.trim().equalsIgnoreCase("ngày") && !str.trim().equalsIgnoreCase("tháng") && !str.trim().equalsIgnoreCase("năm") && !str.trim().equalsIgnoreCase("")
        )).collect(Collectors.toList());

        String soHoaDon = arr2.get(0);
        ZonedDateTime zdt = ZonedDateTime.of(
                ParserUtils.toInt(arr2.get(3)),
                ParserUtils.toInt(arr2.get(2)),
                ParserUtils.toInt(arr2.get(1)),
                0,0,0,0, ZoneId.of("Asia/Ho_Chi_Minh")
        );

        output.put(PhieuNhapKhoHeaderMetadata.SoHoaDon.deAccentedName, soHoaDon);
        output.put(PhieuNhapKhoHeaderMetadata.NgayChungTu.deAccentedName, DateTimeUtils.convertZonedDateTimeToFormat(zdt, "Asia/Ho_Chi_Minh", DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_03)));
        output.put(PhieuNhapKhoHeaderMetadata.NhaCungCap.deAccentedName, ncc);
        return output;
    }

    @SneakyThrows
    public byte[] exportDocuments(List<Map<String, Object>> hoaDonRecords, Map<String, Object> pnkRecords) {
        String folder = this.getOutputFolder();
        String zipPath = this.getOutputZipFolder() + String.format("BIDV - Hồ Sơ Giải Ngân - %s.zip", System.currentTimeMillis());

        this.transformHoaDonTable(hoaDonRecords, folder);
        this.transformPhieuNhapKho(pnkRecords, folder);
        ZipUtils.zipFolder(Paths.get(folder), Paths.get(zipPath));

        return IOUtils.toByteArray(new FileInputStream(zipPath));
    }

    public void transformHoaDonTable(List<Map<String, Object>> records, String folder) {

        Map<String, Object> transformedHoaDon = this.mapByNhaCungCap(records);
        Map<String, Object> mapByNhaCungCap = MapUtils.getMapStringObject(transformedHoaDon, "Map by nhà cung cấp");

        List<Map<String, Object>> sortedListBySttKhongGop = MapUtils.getListMapStringObject(transformedHoaDon, "Số thứ tự không gộp");



        BangKeSuDungTienVayService bangKeSuDungTienVayService = new BangKeSuDungTienVayService(folder, sortedListBySttKhongGop, bangKeSuDungTienVayTemplate);
        bangKeSuDungTienVayService.exportDocument();
        logger.info("Export BangKeSuDungTienVayService");

        DonCamKetService donCamKetService = new DonCamKetService(folder, sortedListBySttKhongGop, donCamKetTemplate);
        donCamKetService.exportDocument();
        logger.info("Export DonCamKetService");

        BienBanKiemTraSuDungVonVayService bienBanKiemTraSuDungVonVayService = new BienBanKiemTraSuDungVonVayService(folder, mapByNhaCungCap, bienBanKiemTraSuDungVonVayTemplate);
        bienBanKiemTraSuDungVonVayService.exportDocument();
        logger.info("Export BienBanKiemTraSuDungVonVayService");

        HopDongTinDungService hopDongTinDungService = new HopDongTinDungService(folder, mapByNhaCungCap, hopDongTinDungTemplate);
        hopDongTinDungService.exportDocument();
        logger.info("Export HopDongTinDungService");


        for (String ncc : mapByNhaCungCap.keySet()) {
            UyNhiemChiService uyNhiemChiService = new UyNhiemChiService(folder, MapUtils.getMapStringObject(mapByNhaCungCap, ncc), uyNhiemChiTemplate);
            uyNhiemChiService.exportDocument();
            logger.info("Export UyNhiemChiService");

        }
    }

    public void transformPhieuNhapKho(Map<String, Object> phieuNhapKhoMap, String folder) {

        for (String ncc : phieuNhapKhoMap.keySet()) {
            Map<String, Object> donHangMap = MapUtils.getMapStringObject(phieuNhapKhoMap, ncc);
            List<Map<String, Object>> listMaHang = new ArrayList<>();
            for (String soHoaDon : donHangMap.keySet()) {
                listMaHang.addAll( MapUtils.getListMapStringObject(donHangMap, soHoaDon));
            }
            DonMuaHangService donMuaHangService = new DonMuaHangService(folder, listMaHang, ncc, donMuaHangTemplate);
            donMuaHangService.exportDocument();
            logger.info("Export DonMuaHangService");
        }
    }

    private Map<String, Object> mapByNhaCungCap(List<Map<String, Object>> records) {
        Map<String, Object> mapByNhaCungCap = new HashMap<>();
        List<Map<String, Object>> sortedBySttKhongGop = new ArrayList<>();
        List<Map<String, Object>> sortedBySttCoGop = new ArrayList<>();
        int sttGop = 0;
        int sttKhongGop = 1;
        for (Map<String, Object> record : records) {
            String nhaCungCap = MapUtils.getString(record, HoaDonHeaderMetadata.NhaCungCap.deAccentedName);
            String soHoaDon = MapUtils.getString(record, HoaDonHeaderMetadata.SoHoaDon.deAccentedName);
            Map<String, Object> dsHoaDonNhaCungCap = MapUtils.getMapStringObject(mapByNhaCungCap, nhaCungCap, new HashMap<>());
            Map<String, Object> hoaDonCuThe = MapUtils.getMapStringObject(dsHoaDonNhaCungCap, soHoaDon, new HashMap<>());

            if (dsHoaDonNhaCungCap.isEmpty()) {
                sttGop++;
            }

            for (HoaDonHeaderMetadata hoaDonHeaderMetadata : HoaDonHeaderMetadata.values()) {
                if (hoaDonHeaderMetadata.isOriginalField) {
                    hoaDonCuThe.put(hoaDonHeaderMetadata.deAccentedName, MapUtils.getString(record, hoaDonHeaderMetadata.deAccentedName));
                } else {
                    switch (hoaDonHeaderMetadata) {
                        case SoThuTuKhongGop: {
                            hoaDonCuThe.put(hoaDonHeaderMetadata.deAccentedName, sttKhongGop);
                            break;
                        }
                    }
                }
            }
            sortedBySttKhongGop.add(hoaDonCuThe);
            sortedBySttCoGop.add(hoaDonCuThe);

            List<String> listSoHoaDon = MapUtils.getListString(dsHoaDonNhaCungCap, HoaDonHeaderMetadata.ListSoHoaDon.deAccentedName, new ArrayList<>());
            listSoHoaDon.add(soHoaDon);
            double tongSoTienThanhToanCacHoaDon = MapUtils.getDouble(dsHoaDonNhaCungCap, HoaDonHeaderMetadata.TongTienThanhToanCacHoaDon.deAccentedName, 0);
            tongSoTienThanhToanCacHoaDon += MapUtils.getDouble(hoaDonCuThe, HoaDonHeaderMetadata.TongTienThanhToan.deAccentedName);


            dsHoaDonNhaCungCap.put(soHoaDon, hoaDonCuThe);
            dsHoaDonNhaCungCap.put(HoaDonHeaderMetadata.ListSoHoaDon.deAccentedName, listSoHoaDon);
            dsHoaDonNhaCungCap.put(HoaDonHeaderMetadata.TongTienThanhToanCacHoaDon.deAccentedName, tongSoTienThanhToanCacHoaDon);
            dsHoaDonNhaCungCap.put(HoaDonHeaderMetadata.SoThuTuCoGop.deAccentedName, sttGop);
            dsHoaDonNhaCungCap.put(HoaDonHeaderMetadata.NhaCungCap.deAccentedName, nhaCungCap);

            mapByNhaCungCap.put(nhaCungCap, dsHoaDonNhaCungCap);

            sttKhongGop++;
        }

        sortedBySttKhongGop.sort(Comparator.comparingInt(m -> MapUtils.getInt(m, HoaDonHeaderMetadata.SoThuTuKhongGop.deAccentedName)));
        sortedBySttCoGop.sort(Comparator.comparingInt(m -> MapUtils.getInt(m, HoaDonHeaderMetadata.SoThuTuCoGop.deAccentedName)));

        return MapUtils.ImmutableMap()
                .put("Số thứ tự có gộp", sortedBySttCoGop)
                .put("Số thứ tự không gộp", sortedBySttKhongGop)
                .put("Map by nhà cung cấp", mapByNhaCungCap)
                .build();
    }


    private void validate(Map<String, Object> output) {
        List<String> headerErrorMessages = this.validateHeaders(MapUtils.getListMapStringObject(output, "headers"));
        List<String> recordErrorMessages = this.validateRecords(MapUtils.getListMapStringObject(output, "records"));
        boolean hasError = false;

        if (!headerErrorMessages.isEmpty()) {
            hasError = true;
            headerErrorMessages.forEach(logger::error);
        }

        if (!recordErrorMessages.isEmpty()) {
            hasError = true;
            recordErrorMessages.forEach(logger::error);
        }

        if (hasError) {
            throw new IllegalArgumentException(String.format("There are %s errors from HeaderValidation and %s errors from RecordValidation", headerErrorMessages.size(), recordErrorMessages.size()));
        }

    }

    private List<String> validateHeaders(List<Map<String, Object>> headers) {

        List<String> messages = new ArrayList<>();

        for (HoaDonHeaderMetadata hoaDonHeaderMetadata : HoaDonHeaderMetadata.values()) {
            boolean found = false;
            if (!hoaDonHeaderMetadata.isOriginalField) {
                continue;
            }
            for (Map<String, Object> headerMap : headers) {
                if (StringUtils
                        .deAccent(MapUtils.getString(headerMap, "name"))
                        .equals(StringUtils.deAccent(hoaDonHeaderMetadata.name))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                messages.add(String.format("Header named '%s' is not found", hoaDonHeaderMetadata.name));
            }
        }

        return messages;
    }

    private List<String> validateRecords(List<Map<String, Object>> records) {
        List<String> messages = new ArrayList<>();

        for (Map<String, Object> record : records) {
            Map<String, Object> deAccentedRecord = this.deAccentAllKeys(record);
            for (HoaDonHeaderMetadata hoaDonHeaderMetadata : HoaDonHeaderMetadata.values()) {
                String val = MapUtils.getString(deAccentedRecord, hoaDonHeaderMetadata.deAccentedName, null);
                if (ParserUtils.isNullOrEmpty(val)) {
                    if (!hoaDonHeaderMetadata.isNullable) {
                        messages.add(String.format(
                                "%s: Value of '%s' cannot be null or empty",
                                MapUtils.getString(deAccentedRecord, HoaDonHeaderMetadata.SoChungTu.deAccentedName),
                                hoaDonHeaderMetadata.name)
                        );
                    }
                } else {
                    if (!hoaDonHeaderMetadata.type.verifyMethod.apply(val)) {
                        messages.add(String.format(
                                    "%s: Value of '%s' which is '%s' cannot be formatted as %s",
                                    MapUtils.getString(deAccentedRecord, HoaDonHeaderMetadata.SoChungTu.deAccentedName),
                                    hoaDonHeaderMetadata.name,
                                    MapUtils.getString(deAccentedRecord, hoaDonHeaderMetadata.deAccentedName),
                                    hoaDonHeaderMetadata.type.javaType.getSimpleName())
                                );
                    }
                }
            }
        }
        return messages;
    }

    private Map<String, Object> deAccentAllKeys(Map<String, Object> record) {
        Map<String, Object> deAccentedRecord = new HashMap<>();
        record.forEach((key, val) -> deAccentedRecord.put(StringUtils.makeCamelCase(key), val));
        return deAccentedRecord;
    }

}