package com.adg.api.department.InternationalPayment.disbursement.bank.mb;

import com.adg.api.department.Accounting.enums.Module;
import com.adg.api.department.Accounting.enums.SlackAuthor;
import com.adg.api.department.Accounting.service.SlackService;
import com.adg.api.department.InternationalPayment.disbursement.bank.mb.writer.DanhSachHoaDonCamHangService;
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
import org.springframework.stereotype.Component;
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
 * Created on: 2022.06.19 23:25
 */
@Component
@Log4j2
public class MbService {

    @Value("${international-payment.bidv.output.files}")
    private String output;

    @Value("${env}")
    private String env;

    @Value("${international-payment.bidv.output.zip}")
    private String outputZipFolder;

    @Value("${international-payment.bidv.input.zip}")
    private String inputZip;

    @Value("${international-payment.mb.output.template.danh-sach-hoa-don-cam-hang}")
    private Resource danhSachHoaDonCamHangTemplate;

    @Autowired
    private PhieuNhapKhoService phieuNhapKhoService;

    @Autowired
    private SlackService slackService;

    public Pair<Map<String, Object>, Map<String, Object>> parseFile(InputStream inputStream) {
        List<File> files = new ArrayList<>();

        List<Map<String, Object>> filesInfo = new ArrayList<>();
        Map<String, Object> pnkStats = new HashMap<>();
        try {
            files = ZipUtils.uncompressZipFile(inputStream, inputZip);
            List<String> phieuNhapKhoFilePaths = new ArrayList<>();
            String type = "pnk";

            for (File f : files) {
                String fileName = f.getName();
                long fileSize = Files.size(f.toPath());
                phieuNhapKhoFilePaths.add(f.getAbsolutePath());
                filesInfo.add(MapUtils.ImmutableMap()
                        .put("type", type)
                        .put("name", fileName)
                        .put("size", fileSize)
                        .build());
            }

            Pair<List<Map<String, Object>>, Map<String, Object>> phieuNhapKhoPair = this.phieuNhapKhoService.parseListPhieuNhapKho(phieuNhapKhoFilePaths);
            pnkStats = phieuNhapKhoPair.getSecond();

            return Pair.of(
                    MapUtils.ImmutableMap()
                            .put("pnk", phieuNhapKhoPair.getFirst())
                            .build(),
                    MapUtils.ImmutableMap()
                            .put("filesInfo", filesInfo)
                            .put("pnkStats", pnkStats)
                            .build()
            );

        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("Error while read Mb File. Exception message: {}. Exception stacktrace: {}", exception.getMessage(), LogUtils.getStackTrace(exception));
            return Pair.of(
                    MapUtils.ImmutableMap()
                            .put("pnk", List.of())
                            .put("exceptionMessage", exception.getMessage())
                            .put("exceptionStackTrace", LogUtils.getStackTrace(exception))
                            .build(),
                    MapUtils.ImmutableMap()
                            .put("filesInfo", filesInfo)
                            .put("pnkStats", pnkStats)
                            .build()
            );
        } finally {
            files.forEach(File::delete);
        }
    }

    public Pair<byte[], Map<String, Object>> generateDisbursementFiles(Map<String, Object> request) {
        Map<String, Object> data = MapUtils.getMapStringObject(request, "data");
        List<Map<String, Object>> phieuNhapKhoRecords = MapUtils.getListMapStringObject(data, "pnk");
        String fileDate = MapUtils.getString(data, "fileDate", DateTimeUtils.convertZonedDateTimeToFormat(ZonedDateTime.now(), "UTC", DateTimeUtils.FMT_09));
        ZonedDateTime zdt = DateTimeUtils.convertStringToZonedDateTime(fileDate, DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_09), "UTC", "UTC");
        return writeFiles(phieuNhapKhoRecords, zdt);
    }

    @SneakyThrows
    private Pair<byte[], Map<String, Object>> writeFiles(List<Map<String, Object>> phieuNhapKhoRecords, ZonedDateTime fileDate) {

        long t1 = System.currentTimeMillis();

        String outputFolder = this.getOutputFolder();
        String zipPath = this.getOutputZipFolder() + String.format("MB - Hồ Sơ Giải Ngân - %s.zip", System.currentTimeMillis());

        Map<String, Object> phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon = this.phieuNhapKhoService.groupPhieuNhapKhoByNhaCungCapAndSoHoaDon(phieuNhapKhoRecords);

        Map<String, Object> stats1 = DanhSachHoaDonCamHangService
                .writeOut(outputFolder, phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon, fileDate, danhSachHoaDonCamHangTemplate);

        ZipUtils.zipFolder(Paths.get(outputFolder), Paths.get(zipPath));

        return Pair
                .of(
                        IOUtils.toByteArray(new FileInputStream(zipPath)),
                        MapUtils.ImmutableMap()
                                .put("duration", DateTimeUtils.getRunningTimeInSecond(t1))
                                .put("statList", List.of(stats1)).build()
                );
    }

    public void sendParseFileNotification(Map<String, Object> payload, long receivedAt, MultipartFile file, Map<String, Object> stats) {
        StringBuilder msgSb = new StringBuilder();

        List<Map<String, Object>> fileMetadatas = MapUtils.getListMapStringObject(stats, "filesInfo");
        Map<String, Object> pnkStats = MapUtils.getMapStringObject(stats, "pnkStats");

        msgSb.append("*--- REQUEST INFORMATION ---*").append("\n");
        msgSb.append(String.format(" - File name: %s", file.getOriginalFilename())).append("\n");
        msgSb.append(String.format(" - File size: %s kb", NumberUtils.formatNumber1(file.getSize()))).append("\n");
        msgSb.append(String.format(" - Content type: %s", file.getContentType())).append("\n\n");

        msgSb.append("*--- PROCESSING STATISTIC ---*").append("\n");
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
        this.slackService.sendNotification(Module.IMPORT_EXPORT, SlackAuthor.LUAN_PHAN, env, String.format("MB - IMPORT - %s", DateTimeUtils.convertZonedDateTimeToFormat(DateTimeUtils.fromEpochMilli(receivedAt, "Asia/Ho_Chi_Minh"), "Asia/Ho_Chi_Minh", DateTimeUtils.FMT_02)), msgSb.toString());
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
        this.slackService.sendNotification(Module.IMPORT_EXPORT, SlackAuthor.LUAN_PHAN, env, String.format("MB - EXPORT - %s", DateTimeUtils.convertZonedDateTimeToFormat(DateTimeUtils.fromEpochMilli(receivedAt, "Asia/Ho_Chi_Minh"), "Asia/Ho_Chi_Minh", DateTimeUtils.FMT_02)), msgSb.toString());
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
