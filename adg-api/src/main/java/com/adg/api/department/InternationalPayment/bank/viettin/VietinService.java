package com.adg.api.department.InternationalPayment.bank.viettin;

import com.adg.api.department.Accounting.enums.Module;
import com.adg.api.department.Accounting.enums.SlackAuthor;
import com.adg.api.department.Accounting.service.SlackService;
import com.adg.api.department.InternationalPayment.bank.viettin.writer.BangKeChungTuDeNghiGiaiNgan.BangKeChungTuDienTuDeNghiGiaiNganService;
import com.adg.api.department.InternationalPayment.bank.viettin.writer.BangKeNopThue.BangKeNopThueService;
import com.adg.api.department.InternationalPayment.bank.viettin.writer.BangKeSuDungTienVay.BangKeSuDungTienVayService;
import com.adg.api.department.InternationalPayment.bank.viettin.writer.GiayNhanNo.GiayNhanNoService;
import com.adg.api.department.InternationalPayment.bank.viettin.writer.UyNhiemChi.UyNhiemChiService;
import com.adg.api.department.InternationalPayment.reader.header.HoaDonHeaderMetadata;
import com.adg.api.department.InternationalPayment.reader.service.HoaDonService;
import com.adg.api.department.InternationalPayment.reader.service.ToKhaiHaiQuanService;
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
 * Created on: 2022.05.28 22:55
 */
@Service
@Log4j2
public class VietinService {

    @Value("${international-payment.bidv.input.zip}")
    private String inputZip;

    @Value("${env}")
    private String env;

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

    @Autowired
    private SlackService slackService;

    public Pair<Map<String, Object>, Map<String, Object>> parseFile(InputStream inputStream) {
        List<File> files = new ArrayList<>();
        List<Map<String, Object>> filesInfo = new ArrayList<>();

        Map<String, Object> tkhqStats = new HashMap<>();
        Map<String, Object> hdStats = new HashMap<>();

        try {
            files = ZipUtils.uncompressZipFile(inputStream, inputZip);
            String hoaDonFilePath = null;
            List<String> toKhaiHaiQuanFilePaths = new ArrayList<>();
            for (File f : files) {
                String type;
                String fileName = f.getName();
                long fileSize = Files.size(f.toPath());

                if (f.getName().toLowerCase().startsWith("tokhai")) {
                    toKhaiHaiQuanFilePaths.add(f.getAbsolutePath());
                    type = "tkhq";
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

            List<Map<String, Object>> tkhqRecords = new ArrayList<>();
            List<Map<String, Object>> hdRecords = new ArrayList<>();


            if (!toKhaiHaiQuanFilePaths.isEmpty()) {
                Pair<List<Map<String, Object>>, Map<String, Object>> toKhaiHaiQuanPair = this.toKhaiHaiQuanService.parseToKhaiHaiQuanFile(toKhaiHaiQuanFilePaths);
                tkhqStats = toKhaiHaiQuanPair.getSecond();
                tkhqRecords = toKhaiHaiQuanPair.getFirst();
            }

            if (!ParserUtils.isNullOrEmpty(hoaDonFilePath)) {
                Pair<List<Map<String, Object>>, Map<String, Object>> hoaDonPair = this.hoaDonService.parseHoaDonFile(hoaDonFilePath);
                hdStats = hoaDonPair.getSecond();
                hdRecords = hoaDonPair.getFirst();
            }

            return Pair.of(
                    MapUtils.ImmutableMap()
                            .put("hd", hdRecords)
                            .put("tkhq", tkhqRecords)
                            .build(),
                    MapUtils.ImmutableMap()
                            .put("filesInfo", filesInfo)
                            .put("hdStats", hdStats)
                            .put("tkhqStats", tkhqStats)
                            .build()
            );

        } catch (Exception exception) {
            log.error("Error while read Vietin File. Exception message: {}. Exception stacktrace: {}", exception.getMessage(), LogUtils.getStackTrace(exception));
            return Pair.of(
                    MapUtils.ImmutableMap()
                            .put("hd", List.of())
                            .put("tkhq", List.of())
                            .put("exceptionMessage", exception.getMessage())
                            .put("exceptionStackTrace", LogUtils.getStackTrace(exception))
                            .build(),
                    MapUtils.ImmutableMap()
                            .put("filesInfo", filesInfo)
                            .put("hdStats", hdStats)
                            .put("tkhqStats", tkhqStats)
                            .build()
            );
        } finally {
            files.forEach(File::delete);
        }

    }

    public Pair<byte[], Map<String, Object>> generateDisbursementFiles(Map<String, Object> request) {
        Map<String, Object> data = MapUtils.getMapStringObject(request, "data");
        List<Map<String, Object>> hoaDon = MapUtils.getListMapStringObject(data, "hd");
        List<Map<String, Object>> toKhaiHaiQuan = MapUtils.getListMapStringObject(data, "tkhq");
        String fileDate = MapUtils.getString(data, "fileDate", DateTimeUtils.convertZonedDateTimeToFormat(ZonedDateTime.now(), "UTC", DateTimeUtils.FMT_09));
        ZonedDateTime zdt = DateTimeUtils.convertStringToZonedDateTime(fileDate, DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_09), "UTC", "UTC");

        return writeFiles(hoaDon, toKhaiHaiQuan, zdt);
    }

    @SneakyThrows
    private Pair<byte[], Map<String, Object>> writeFiles(List<Map<String, Object>> hoaDonRecords, List<Map<String, Object>> toKhaiHaiQuanRecords, ZonedDateTime fileDate) {
        long t1 = System.currentTimeMillis();

        String outputFolder = this.getOutputFolder();
        String zipPath = this.getOutputZipFolder() + String.format("VIETIN - Hồ Sơ Giải Ngân - %s.zip", System.currentTimeMillis());

        Map<String, Object> hoaDonRecordsGroupByNhaCungCap = this.groupHoaDonByNCC(hoaDonRecords);
        Map<String, Object> toKhaiHaiQuanRecordsGroupBySoToKhai = this.toKhaiHaiQuanService.groupToKhaiHaiQuanRecordsBySoToKhai(toKhaiHaiQuanRecords);


        Map<String, Object> stats1 = BangKeChungTuDienTuDeNghiGiaiNganService
                .writeOut(outputFolder, hoaDonRecords, toKhaiHaiQuanRecords, fileDate, bangKeChungTuDienTuDeNghiGiaiNganTemplate);

        Map<String, Object> stats2 = BangKeSuDungTienVayService
                .writeOut(outputFolder, hoaDonRecords, toKhaiHaiQuanRecords, fileDate, bangKeSuDungTienVayTemplate);

        Map<String, Object> stats3 = GiayNhanNoService
                .writeOut(outputFolder, hoaDonRecords, toKhaiHaiQuanRecords, fileDate, giayNhanNoTemplate);

        Map<String, Object> stats4 = UyNhiemChiService
                .writeOut(outputFolder, hoaDonRecordsGroupByNhaCungCap, fileDate, uyNhiemChiTemplate);

        Map<String, Object> stats5 = BangKeNopThueService
                .writeOut(outputFolder, toKhaiHaiQuanRecordsGroupBySoToKhai, fileDate, bangKeNopThueTemplate);

        ZipUtils.zipFolder(Paths.get(outputFolder), Paths.get(zipPath));

        return Pair
                .of(
                        IOUtils.toByteArray(new FileInputStream(zipPath)),
                        MapUtils.ImmutableMap()
                                .put("duration", DateTimeUtils.getRunningTimeInSecond(t1))
                                .put("statList", List.of(stats1, stats2, stats3, stats4, stats5)).build()
                );
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
            detailStepMsg = ParserUtils.isNullOrEmpty(detailStepMsg) ? "\n" : "````" + detailStepMsg + "```";
            return String.format("- Step %s: *%s*\n- Duration: %s\n- Generated files: %s file(s)\n- Detail: %s", stepIndex, step, stepDuration, detailStep.size(), detailStepMsg);
        }).collect(Collectors.joining("\n---\n"));
        msgSb.append(statMsg).append("\n\n");
        this.slackService.sendNotification(Module.IMPORT_EXPORT, SlackAuthor.LUAN_PHAN, env, String.format("VIETIN - EXPORT - %s", DateTimeUtils.convertZonedDateTimeToFormat(DateTimeUtils.fromEpochMilli(receivedAt, "Asia/Ho_Chi_Minh"), "Asia/Ho_Chi_Minh", DateTimeUtils.FMT_02)), msgSb.toString());
    }

    public void sendParseFileNotification(Map<String, Object> payload, long receivedAt, MultipartFile file, Map<String, Object> stats) {
        StringBuilder msgSb = new StringBuilder();

        List<Map<String, Object>> fileMetadatas = MapUtils.getListMapStringObject(stats, "filesInfo");
        Map<String, Object> hdStats = MapUtils.getMapStringObject(stats, "hdStats");
        Map<String, Object> tkhqStats = MapUtils.getMapStringObject(stats, "tkhqStats");

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

        String pnkDetail = MapUtils.getListMapStringObject(tkhqStats, "detailStats").stream()
                .map(detailStat -> String.format("%s (%s kb): %s record(s) - %s", MapUtils.getString(detailStat, "fileName"), MapUtils.getString(detailStat, "fileSize"), MapUtils.getString(detailStat, "recordSize"), MapUtils.getString(detailStat, "parseDuration")))
                .collect(Collectors.joining("\n         > ", "\n         > ", ""));
        msgSb.append(" - Handle To Khai Hai Quan").append("\n");
        msgSb.append(String.format("      + Parse duration: %s", MapUtils.getString(tkhqStats, "parseDuration"))).append("\n");
        msgSb.append(String.format("      + Record size: %s", MapUtils.getString(tkhqStats, "totalRecords"))).append("\n");
        msgSb.append(String.format("      + Detail: %s", pnkDetail)).append("\n\n");

        msgSb.append("*--- RESPONSE INFORMATION ---*").append("\n");
        msgSb.append(String.format(" - Duration: %s", DateTimeUtils.getRunningTimeInSecond(receivedAt))).append("\n");
        msgSb.append(String.format(" - Response body: ```%s```", JsonUtils.toJson(payload))).append("\n");
        this.slackService.sendNotification(Module.IMPORT_EXPORT, SlackAuthor.LUAN_PHAN, env, String.format("VIETIN - IMPORT - %s", DateTimeUtils.convertZonedDateTimeToFormat(DateTimeUtils.fromEpochMilli(receivedAt, "Asia/Ho_Chi_Minh"), "Asia/Ho_Chi_Minh", DateTimeUtils.FMT_02)), msgSb.toString());
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
