package com.adg.api.department.InternationalPayment.service.bidv;

import com.adg.api.department.InternationalPayment.service.bidv.reader.HoaDonService;
import com.adg.api.department.InternationalPayment.service.bidv.reader.PhieuNhapKhoService;
import com.adg.api.department.InternationalPayment.service.bidv.writer.BangKeSuDungTienVay.BangKeSuDungTienVayService;
import com.adg.api.department.InternationalPayment.service.bidv.writer.BienBanKiemTraSuDungVonVay.BienBanKiemTraSuDungVonVayService;
import com.adg.api.department.InternationalPayment.service.bidv.writer.DonCamKet.DonCamKetService;
import com.adg.api.department.InternationalPayment.service.bidv.writer.DonMuaHang.DonMuaHangService;
import com.adg.api.department.InternationalPayment.service.bidv.writer.HopDongTinDung.HopDongTinDungService;
import com.adg.api.department.InternationalPayment.service.bidv.writer.UyNhiemChi.UyNhiemChiService;
import com.adg.api.util.ZipUtils;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.06.03 12:43
 */
@Service
@Log4j2
public class BidvService {

    @Value("${international-payment.bidv.output.files}")
    private String output;

    @Value("${international-payment.bidv.output.zip}")
    private String outputZipFolder;

    @Value("${international-payment.bidv.output.template.bang-ke-su-dung-tien-vay}")
    private Resource bangKeSuDungTienVayTemplate;

    @Value("${international-payment.bidv.output.template.bien-ban-kiem-tra-su-dung-von-vay}")
    private Resource bienBanKiemTraSuDungVonVayTemplate;

    @Value("${international-payment.bidv.output.template.don-cam-ket}")
    private Resource donCamKetTemplate;

    @Value("${international-payment.bidv.output.template.don-mua-hang}")
    private Resource donMuaHangTemplate;

    @Value("${international-payment.bidv.output.template.hop-dong-tin-dung}")
    private Resource hopDongTinDungTemplate;

    @Value("${international-payment.bidv.output.template.uy-nhiem-chi}")
    private Resource uyNhiemChiTemplate;

    @Value("${international-payment.bidv.input.zip}")
    private String inputZip;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private PhieuNhapKhoService phieuNhapKhoService;

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

    public Map<String, Object> parseFile(InputStream inputStream) {
        List<File> files = new ArrayList<>();
        try {
            files = ZipUtils.uncompressZipFile(inputStream, inputZip);
            String hoaDonFilePath = "";
            List<String> phieuNhapKhoFilePaths = new ArrayList<>();
            for (File f : files) {
                if (f.getName().toLowerCase().startsWith("pnk")) {
                    phieuNhapKhoFilePaths.add(f.getAbsolutePath());
                } else {
                    hoaDonFilePath = f.getAbsolutePath();
                }
            }

            List<Map<String, Object>> hoaDonRecords = this.hoaDonService.parseHoaDonFile(hoaDonFilePath);
            List<Map<String, Object>> phieuNhapKhoRecords = this.phieuNhapKhoService.readPhieuNhapKho(phieuNhapKhoFilePaths);

            return MapUtils.ImmutableMap()
                    .put("hd", hoaDonRecords)
                    .put("pnk", phieuNhapKhoRecords)
                    .build();

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            files.forEach(File::delete);
        }
        return MapUtils.ImmutableMap().build();
    }

    public byte[] generateDisbursementFiles(Map<String, Object> request) {
        Map<String, Object> data = MapUtils.getMapStringObject(request, "data");
        List<Map<String, Object>> hoaDonRecords = MapUtils.getListMapStringObject(data, "hd");
        List<Map<String, Object>> phieuNhapKhoRecords = MapUtils.getListMapStringObject(data, "pnk");
        String fileDate = MapUtils.getString(data, "fileDate", DateTimeUtils.convertZonedDateTimeToFormat(ZonedDateTime.now(), "UTC", DateTimeUtils.FMT_09));
        ZonedDateTime zdt = DateTimeUtils.convertStringToZonedDateTime(fileDate, DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_09), "UTC", "UTC");
        String contractNumber = MapUtils.getString(data, "contractNumber");

        return writeFiles(hoaDonRecords, phieuNhapKhoRecords, zdt, contractNumber);
    }

    @SneakyThrows
    private byte[] writeFiles(List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> phieuNhapKhoRecords, ZonedDateTime fileDate, String contractNumber) {

        String outputFolder = this.getOutputFolder();
        String zipPath = this.getOutputZipFolder() + String.format("BIDV - Hồ Sơ Giải Ngân - %s.zip", System.currentTimeMillis());

        Pair<Map<String, Object>, List<Map<String, Object>>> hoaDonPair = this.hoaDonService.transformHoaDonRecords(hoaDonRecords);

        Map<String, Object> phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon = this.phieuNhapKhoService.groupPhieuNhapKhoByNhaCungCapAndSoHoaDon(phieuNhapKhoRecords);

        Map<String, Object> hoaDonRecordsGroupByNhaCungCap = hoaDonPair.getFirst();
        List<Map<String, Object>> hoaDonRecordsSortBySttKhongGop = hoaDonPair.getSecond();


        BangKeSuDungTienVayService
                .writeOut(outputFolder, hoaDonRecordsSortBySttKhongGop, fileDate, contractNumber, bangKeSuDungTienVayTemplate);

        DonCamKetService
                .writeOut(outputFolder, hoaDonRecordsSortBySttKhongGop, fileDate, donCamKetTemplate);

        BienBanKiemTraSuDungVonVayService
                .writeOut(outputFolder, hoaDonRecordsGroupByNhaCungCap, fileDate, contractNumber, bienBanKiemTraSuDungVonVayTemplate);

        HopDongTinDungService
                .writeOut(outputFolder, hoaDonRecordsGroupByNhaCungCap, fileDate, contractNumber, hopDongTinDungTemplate);

        UyNhiemChiService
                .writeOut(outputFolder, hoaDonRecordsGroupByNhaCungCap, fileDate, uyNhiemChiTemplate);

        DonMuaHangService
                .writeOut(outputFolder, hoaDonRecordsGroupByNhaCungCap, phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon, fileDate, donMuaHangTemplate);

        ZipUtils.zipFolder(Paths.get(outputFolder), Paths.get(zipPath));

        return IOUtils.toByteArray(new FileInputStream(zipPath));

    }


}
