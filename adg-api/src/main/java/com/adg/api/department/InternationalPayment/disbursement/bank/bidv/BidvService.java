package com.adg.api.department.InternationalPayment.disbursement.bank.bidv;

import com.adg.api.department.Accounting.enums.Module;
import com.adg.api.department.Accounting.enums.SlackAuthor;
import com.adg.api.department.Accounting.service.SlackService;
import com.adg.api.department.InternationalPayment.disbursement.bank.bidv.writer.BangKeSuDungTienVay.BangKeSuDungTienVayService;
import com.adg.api.department.InternationalPayment.disbursement.bank.bidv.writer.BienBanKiemTraSuDungVonVay.BienBanKiemTraSuDungVonVayService;
import com.adg.api.department.InternationalPayment.disbursement.bank.bidv.writer.DonCamKet.DonCamKetService;
import com.adg.api.department.InternationalPayment.disbursement.bank.bidv.writer.DonMuaHang.DonMuaHangService;
import com.adg.api.department.InternationalPayment.disbursement.bank.bidv.writer.HopDongTinDung.HopDongTinDungService;
import com.adg.api.department.InternationalPayment.disbursement.bank.bidv.writer.UyNhiemChi.UyNhiemChiService;
import com.adg.api.department.InternationalPayment.disbursement.reader.service.HoaDonService;
import com.adg.api.department.InternationalPayment.disbursement.reader.service.PhieuNhapKhoService;
import com.adg.api.util.ZipUtils;
import com.merlin.asset.core.utils.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.06.03 12:43
 */
@Service
@Log4j2
public class BidvService {

    @Value("${international-payment.bidv.output.files}")
    private String output;

    @Value("${env}")
    private String env;

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

    @Autowired
    private SlackService slackService;

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

    public Pair<Map<String, Object>, Map<String, Object>> parseFile(InputStream inputStream) {
        List<File> files = new ArrayList<>();

        List<Map<String, Object>> filesInfo = new ArrayList<>();
        Map<String, Object> pnkStats = new HashMap<>();
        Map<String, Object> hdStats = new HashMap<>();
        try {
            files = ZipUtils.uncompressZipFile(inputStream, inputZip);
            String hoaDonFilePath = "";
            List<String> phieuNhapKhoFilePaths = new ArrayList<>();
            for (File f : files) {
                String type;
                String fileName = f.getName();
                long fileSize = Files.size(f.toPath());
                if (f.getName().toLowerCase().startsWith("pnk")) {
                    phieuNhapKhoFilePaths.add(f.getAbsolutePath());
                    type = "pnk";
                } else {
                    hoaDonFilePath = f.getAbsolutePath();
                    type = "hd";
                }
                filesInfo.add(MapUtils.ImmutableMap()
                        .put("type", type)
                        .put("name", fileName)
                        .put("size", fileSize)
                        .build());
            }

            Pair<List<Map<String, Object>>, Map<String, Object>> hoaDonPair = this.hoaDonService.parseHoaDonFile(hoaDonFilePath);
            hdStats = hoaDonPair.getSecond();
            Pair<List<Map<String, Object>>, Map<String, Object>> phieuNhapKhoPair = this.phieuNhapKhoService.parseListPhieuNhapKho(phieuNhapKhoFilePaths);
            pnkStats = phieuNhapKhoPair.getSecond();


            return Pair.of(
                    MapUtils.ImmutableMap()
                            .put("hd", hoaDonPair.getFirst())
                            .put("pnk", phieuNhapKhoPair.getFirst())
                            .build(),
                    MapUtils.ImmutableMap()
                            .put("filesInfo", filesInfo)
                            .put("hdStats", hdStats)
                            .put("pnkStats", pnkStats)
                            .build()
            );

        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("Error while read Bidv File. Exception message: {}. Exception stacktrace: {}", exception.getMessage(), LogUtils.getStackTrace(exception));
            return Pair.of(
                    MapUtils.ImmutableMap()
                            .put("hd", List.of())
                            .put("pnk", List.of())
                            .put("exceptionMessage", exception.getMessage())
                            .put("exceptionStackTrace", LogUtils.getStackTrace(exception))
                            .build(),
                    MapUtils.ImmutableMap()
                            .put("filesInfo", filesInfo)
                            .put("hdStats", hdStats)
                            .put("pnkStats", pnkStats)
                            .build()
            );
        } finally {
            files.forEach(File::delete);
        }
    }

    public void sendParseFileNotification(Map<String, Object> payload, long receivedAt, MultipartFile file, Map<String, Object> stats) {
        StringBuilder msgSb = new StringBuilder();

        List<Map<String, Object>> fileMetadatas = MapUtils.getListMapStringObject(stats, "filesInfo");
        Map<String, Object> hdStats = MapUtils.getMapStringObject(stats, "hdStats");
        Map<String, Object> pnkStats = MapUtils.getMapStringObject(stats, "pnkStats");

        String fileMetadataStr = fileMetadatas.stream().map(fileMetadata ->
                String.format("   > %s - %s - %s",
                        MapUtils.getString(fileMetadata, "type"),
                        MapUtils.getString(fileMetadata, "name"),
                        NumberUtils.formatNumber1(MapUtils.getLong(fileMetadata, "size")) + " kb"
                        )
        ).collect(Collectors.joining("\n"));

        msgSb.append("*--- REQUEST INFORMATION ---*").append("\n");
        msgSb.append(String.format(" - File name: %s", file.getOriginalFilename())).append("\n");
        msgSb.append(String.format(" - File size: %s kb", NumberUtils.formatNumber1(file.getSize()))).append("\n");
        msgSb.append(String.format(" - Content type: %s", file.getContentType())).append("\n\n");

        msgSb.append("*--- PROCESSING STATISTIC ---*").append("\n");
        msgSb.append(" - Handle Hoa Don").append("\n");
        msgSb.append(String.format("      + File name: %s", MapUtils.getString(hdStats, "fileName"))).append("\n");
        msgSb.append(String.format("      + File size: %s kb", MapUtils.getString(hdStats, "fileSize"))).append("\n");
        msgSb.append(String.format("      + Parse duration: %s", MapUtils.getString(hdStats, "parseDuration"))).append("\n");
        msgSb.append(String.format("      + Record size: %s", MapUtils.getString(hdStats, "recordSize"))).append("\n\n");

        String pnkDetail = MapUtils.getListMapStringObject(pnkStats, "detailStats").stream()
                .map(detailStat -> String.format("%s (%s kb): %s record(s) - %s", MapUtils.getString(detailStat, "fileName"), MapUtils.getString(detailStat, "fileSize"), MapUtils.getString(detailStat, "recordSize"), MapUtils.getString(detailStat, "parseDuration")))
                        .collect(Collectors.joining("\n         > ", "\n         > ", ""));
        msgSb.append(" - Handle Phieu Nhap Kho").append("\n");
        msgSb.append(String.format("      + Parse duration: %s", MapUtils.getString(pnkStats, "parseDuration"))).append("\n");
        msgSb.append(String.format("      + Record size: %s", MapUtils.getString(pnkStats, "totalRecords"))).append("\n");
        msgSb.append(String.format("      + Detail: %s", pnkDetail)).append("\n\n");

        msgSb.append("*--- RESPONSE INFORMATION ---*").append("\n");
        msgSb.append(String.format(" - Duration: %s", DateTimeUtils.getRunningTimeInSecond(receivedAt))).append("\n");
        msgSb.append(String.format(" - Response body: ```%s```", JsonUtils.toJson(payload))).append("\n");
        this.slackService.sendNotification(Module.IMPORT_EXPORT, SlackAuthor.LUAN_PHAN, env, String.format("BIDV - IMPORT - %s", DateTimeUtils.convertZonedDateTimeToFormat(DateTimeUtils.fromEpochMilli(receivedAt, "Asia/Ho_Chi_Minh"), "Asia/Ho_Chi_Minh", DateTimeUtils.FMT_02)), msgSb.toString());
    }

    public void sendGenerateDisbursementFilesNotification(Map<String, Object> request, long receivedAt, Map<String, Object> map) {
        StringBuilder msgSb = new StringBuilder();

        List<Map<String, Object>> statsList = MapUtils.getListMapStringObject(map, "statList");
        String duration = MapUtils.getString(map, "duration");

        msgSb.append("*--- GENERAL INFORMATION ---*").append("\n");
        msgSb.append(String.format("Running Steps: %s", statsList.size())).append("\n");
        msgSb.append(String.format("Duration: %s", duration)).append("\n\n\n");

        msgSb.append("*--- DETAIL ---*").append("\n");
        AtomicInteger stepIndex = new AtomicInteger();
        String statMsg = statsList.stream().map(stat -> {
            stepIndex.getAndIncrement();
            String step = MapUtils.getString(stat, "step");
            String stepDuration = MapUtils.getString(stat, "duration");
            List<Map<String, Object>> detailStep = MapUtils.getListMapStringObject(stat, "detail");
            String detailStepMsg = detailStep.stream().map(detail -> {
                String fillTableDuration = MapUtils.getString(detail, "fillTableDuration", "none");
                String fillOtherDataDuration = MapUtils.getString(detail, "fillOtherDataDuration", "none");
                String fileName = MapUtils.getString(detail, "fileName");
                String writeFileDuration = MapUtils.getString(detail, "writeFileDuration", "none");
                return String.format("File Name: %s\nDuration of Fill Table/Fill Other/Write File: %s/%s/%s", fileName, fillTableDuration, fillOtherDataDuration, writeFileDuration);
            }).collect(Collectors.joining("\n---\n"));
            return String.format("- Step %s: *%s*\n- Duration: %s\n- Generated files: %s file(s)\n- Detail: ```%s```", stepIndex, step, stepDuration, detailStep.size(), detailStepMsg);
        }).collect(Collectors.joining("\n---\n"));
        msgSb.append(statMsg).append("\n\n");
        this.slackService.sendNotification(Module.IMPORT_EXPORT, SlackAuthor.LUAN_PHAN, env, String.format("BIDV - EXPORT - %s", DateTimeUtils.convertZonedDateTimeToFormat(DateTimeUtils.fromEpochMilli(receivedAt, "Asia/Ho_Chi_Minh"), "Asia/Ho_Chi_Minh", DateTimeUtils.FMT_02)), msgSb.toString());
    }

    public Pair<byte[], Map<String, Object>> generateDisbursementFiles(Map<String, Object> request) {
        Map<String, Object> data = MapUtils.getMapStringObject(request, "data");
        List<Map<String, Object>> hoaDonRecords = MapUtils.getListMapStringObject(data, "hd");
        List<Map<String, Object>> phieuNhapKhoRecords = MapUtils.getListMapStringObject(data, "pnk");
        String fileDate = MapUtils.getString(data, "fileDate", DateTimeUtils.convertZonedDateTimeToFormat(ZonedDateTime.now(), "UTC", DateTimeUtils.FMT_09));
        ZonedDateTime zdt = DateTimeUtils.convertStringToZonedDateTime(fileDate, DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_09), "UTC", "UTC");
        String contractNumber = MapUtils.getString(data, "contractNumber");

        return writeFiles(hoaDonRecords, phieuNhapKhoRecords, zdt, contractNumber);
    }

    @SneakyThrows
    private Pair<byte[], Map<String, Object>> writeFiles(List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> phieuNhapKhoRecords, ZonedDateTime fileDate, String contractNumber) {

        long t1 = System.currentTimeMillis();

        String outputFolder = this.getOutputFolder();
        String zipPath = this.getOutputZipFolder() + String.format("BIDV - Hồ Sơ Giải Ngân - %s.zip", System.currentTimeMillis());

        Pair<Map<String, Object>, List<Map<String, Object>>> hoaDonPair = this.hoaDonService.transformHoaDonRecords(hoaDonRecords);

        Map<String, Object> phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon = this.phieuNhapKhoService.groupPhieuNhapKhoByNhaCungCapAndSoHoaDon(phieuNhapKhoRecords);

        Map<String, Object> hoaDonRecordsGroupByNhaCungCap = hoaDonPair.getFirst();
        List<Map<String, Object>> hoaDonRecordsSortBySttKhongGop = hoaDonPair.getSecond();


        Map<String, Object> stats1 = BangKeSuDungTienVayService
                .writeOut(outputFolder, hoaDonRecordsSortBySttKhongGop, fileDate, contractNumber, bangKeSuDungTienVayTemplate);

        Map<String, Object> stats2 = DonCamKetService
                .writeOut(outputFolder, hoaDonRecordsSortBySttKhongGop, fileDate, donCamKetTemplate);

        Map<String, Object> stats3 = BienBanKiemTraSuDungVonVayService
                .writeOut(outputFolder, hoaDonRecordsGroupByNhaCungCap, fileDate, contractNumber, bienBanKiemTraSuDungVonVayTemplate);

        Map<String, Object> stats4 = HopDongTinDungService
                .writeOut(outputFolder, hoaDonRecordsGroupByNhaCungCap, fileDate, contractNumber, hopDongTinDungTemplate);

        Map<String, Object> stats5 = UyNhiemChiService
                .writeOut(outputFolder, hoaDonRecordsGroupByNhaCungCap, fileDate, uyNhiemChiTemplate);

        Map<String, Object> stats6 = DonMuaHangService
                .writeOut(outputFolder, hoaDonRecordsGroupByNhaCungCap, phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon, fileDate, donMuaHangTemplate);

        ZipUtils.zipFolder(Paths.get(outputFolder), Paths.get(zipPath));

        return Pair
                .of(
                        IOUtils.toByteArray(new FileInputStream(zipPath)),
                        MapUtils.ImmutableMap()
                                .put("duration", DateTimeUtils.getRunningTimeInSecond(t1))
                                .put("statList", List.of(stats1, stats2, stats3, stats4, stats5, stats6)).build()
                );
    }


}
