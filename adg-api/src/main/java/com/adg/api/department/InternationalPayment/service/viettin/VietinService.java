package com.adg.api.department.InternationalPayment.service.viettin;

import com.adg.api.department.InternationalPayment.service.bidv.enums.HoaDonHeaderMetadata;
import com.adg.api.department.InternationalPayment.service.bidv.reader.HoaDonService;
import com.adg.api.department.InternationalPayment.service.viettin.reader.ToKhaiHaiQuanService;
import com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeChungTuDeNghiGiaiNgan.BangKeChungTuDienTuDeNghiGiaiNganService;
import com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeNopThue.BangKeNopThueService;
import com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeSuDungTienVay.BangKeSuDungTienVayService;
import com.adg.api.department.InternationalPayment.service.viettin.writer.GiayNhanNo.GiayNhanNoService;
import com.adg.api.department.InternationalPayment.service.viettin.writer.UyNhiemChi.UyNhiemChiService;
import com.adg.api.util.ZipUtils;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.28 22:55
 */
@Service
public class VietinService {

    @Value("${international-payment.bidv.input.zip}")
    private String inputZip;

    @Value("${international-payment.bidv.output.files}")
    private String output;

    @Value("${international-payment.bidv.output.zip}")
    private String outputZipFolder;

    @Value("${international-payment.viettin.output.template.bang-ke-chung-tu-dien-tu-de-nghi-giai-ngan}")
    private Resource bangKeChungTuDienTuDeNghiGiaiNganTemplate;

    @Value("${international-payment.viettin.output.template.bang-ke-su-dung-tien-vay}")
    private Resource bangKeSuDungTienVayTemplate;

    @Value("${international-payment.viettin.output.template.uy-nhiem-chi}")
    private Resource uyNhiemChiTemplate;

    @Value("${international-payment.viettin.output.template.giay-nhan-no}")
    private Resource giayNhanNoTemplate;

    @Value("${international-payment.viettin.output.template.bang-ke-nop-thue}")
    private Resource bangKeNopThueTemplate;

    @Autowired
    private ToKhaiHaiQuanService toKhaiHaiQuanService;

    @Autowired
    private HoaDonService hoaDonService;

    public Map<String, Object> parseFile(InputStream inputStream) {
        List<File> files = new ArrayList<>();
        try {
            files = ZipUtils.uncompressZipFile(inputStream, inputZip);
            String fileHoaDon = "";
            List<String> fileTKHQ = new ArrayList<>();
            for (File f : files) {
                if (f.getName().toLowerCase().startsWith("tokhai")) {
                    fileTKHQ.add(f.getAbsolutePath());
                } else {
                    fileHoaDon = f.getAbsolutePath();
                }
            }

            List<Map<String, Object>> toKhaiHaiQuan = this.toKhaiHaiQuanService.readToKhaiHaiQuan(fileTKHQ);
            List<Map<String, Object>> hoaDon = this.hoaDonService.parseHoaDonFile(fileHoaDon).getFirst();

            return MapUtils.ImmutableMap()
                    .put("tkhq", toKhaiHaiQuan)
                    .put("hd", hoaDon)
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
        List<Map<String, Object>> hoaDon = MapUtils.getListMapStringObject(data, "hd");
        List<Map<String, Object>> toKhaiHaiQuan = MapUtils.getListMapStringObject(data, "tkhq");
        String fileDate = MapUtils.getString(data, "fileDate", DateTimeUtils.convertZonedDateTimeToFormat(ZonedDateTime.now(), "UTC", DateTimeUtils.FMT_09));
        ZonedDateTime zdt = DateTimeUtils.convertStringToZonedDateTime(fileDate, DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_09), "UTC", "UTC");

        return writeFiles(hoaDon, toKhaiHaiQuan, zdt);
    }

    @SneakyThrows
    private byte[] writeFiles(List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> toKhaiHaiQuanRecords, ZonedDateTime fileDate) {
        String outputFolder = this.getOutputFolder();
        String zipPath = this.getOutputZipFolder() + String.format("VIETIN - Hồ Sơ Giải Ngân - %s.zip", System.currentTimeMillis());

        Map<String, Object> hoaDonRecordsGroupByNhaCungCap = this.groupHoaDonByNCC(hoaDonRecords);
        Map<String, Object> toKhaiHaiQuanRecordsGroupBySoToKhai = this.toKhaiHaiQuanService.groupToKhaiHaiQuanRecordsBySoToKhai(toKhaiHaiQuanRecords);


        BangKeChungTuDienTuDeNghiGiaiNganService
                .writeOut(outputFolder, hoaDonRecords, toKhaiHaiQuanRecords, fileDate, bangKeChungTuDienTuDeNghiGiaiNganTemplate);

        BangKeSuDungTienVayService
                .writeOut(outputFolder, hoaDonRecords, toKhaiHaiQuanRecords, fileDate, bangKeSuDungTienVayTemplate);

        GiayNhanNoService
                .writeOut(outputFolder, hoaDonRecords, toKhaiHaiQuanRecords, fileDate, giayNhanNoTemplate);

        UyNhiemChiService
                .writeOut(outputFolder, hoaDonRecordsGroupByNhaCungCap, fileDate, uyNhiemChiTemplate);

        BangKeNopThueService
                .writeOut(outputFolder, toKhaiHaiQuanRecordsGroupBySoToKhai, fileDate, bangKeNopThueTemplate);

        ZipUtils.zipFolder(Paths.get(outputFolder), Paths.get(zipPath));

        return IOUtils.toByteArray(new FileInputStream(zipPath));
    }

    private Map<String, Object> groupHoaDonByNCC(List<Map<String, Object>> hoaDonRecords) {
        Map<String, Object> result = new HashMap<>();
        for (Map<String, Object> hoaDonRecord : hoaDonRecords) {
            String nhaCungCap = MapUtils.getString(hoaDonRecord, HoaDonHeaderMetadata.NhaCungCap.deAccentedName);
            List<Map<String, Object>> danhSachHoaDon = MapUtils.getListMapStringObject(result, nhaCungCap);
            danhSachHoaDon.add(hoaDonRecord);
            result.put(nhaCungCap, danhSachHoaDon);
        }
        return result;
    }


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
}
