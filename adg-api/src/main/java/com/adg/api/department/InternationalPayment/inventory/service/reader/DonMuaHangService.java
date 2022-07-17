package com.adg.api.department.InternationalPayment.inventory.service.reader;

import com.adg.api.department.Accounting.enums.Module;
import com.adg.api.department.Accounting.enums.SlackAuthor;
import com.adg.api.department.Accounting.service.SlackService;
import com.adg.api.department.InternationalPayment.disbursement.office.excel.ExcelReader;
import com.adg.api.department.InternationalPayment.inventory.dto.FilePurchaseOrderDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.PurchaseOrderDTO;
import com.adg.api.department.InternationalPayment.inventory.service.BankService;
import com.adg.api.util.ZipUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merlin.asset.core.utils.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 00:27
 */
@Log4j2
@Service
public class DonMuaHangService {

    @Value("${international-payment.bidv.input.zip}")
    private String inputZip;

    @Autowired
    private SlackService slackService;

    @Value("${env}")
    private String env;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private BankService bankService;


    public Pair<List<PurchaseOrderDTO>, Map<String, Object>> parseFile(InputStream inputStream) {
        List<File> files = new ArrayList<>();
        List<Map<String, Object>> filesInfo = new ArrayList<>();
        Map<String, Object> dmhStats = new HashMap<>();
        try {
            files = ZipUtils.uncompressZipFile(inputStream, inputZip);
            String donMuaHangFilePath = "";
            for (File f : files) {
                String fileName = f.getName();
                long fileSize = Files.size(f.toPath());
                if (f.getName().toLowerCase().endsWith("xls") || f.getName().toLowerCase().endsWith("xlsx")) {
                    donMuaHangFilePath = f.getAbsolutePath();
                    filesInfo.add(MapUtils.ImmutableMap()
                            .put("name", fileName)
                            .put("size", fileSize)
                            .build());
                    break;
                }
            }

            Pair<List<Map<String, Object>>, Map<String, Object>> donMuaHangPair = this.parseDonMuaHangFile(donMuaHangFilePath);
            dmhStats = donMuaHangPair.getSecond();
            List<PurchaseOrderDTO> purchaseOrderDTO = objectMapper.readValue(JsonUtils.toJson(donMuaHangPair.getFirst()), new TypeReference<>() {});
            return Pair.of(
                    purchaseOrderDTO,
                    MapUtils.ImmutableMap()
                            .put("filesInfo", filesInfo)
                            .put("dmhStats", dmhStats)
                            .build()
            );

        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("Error while read Bidv File. Exception message: {}. Exception stacktrace: {}", exception.getMessage(), LogUtils.getStackTrace(exception));
            return Pair.of(
                    List.of(),
                    MapUtils.ImmutableMap()
                            .put("filesInfo", filesInfo)
                            .put("dmhStats", dmhStats)
                            .build()
            );
        } finally {
            files.forEach(File::delete);
        }
    }

    @SneakyThrows
    private Pair<List<Map<String, Object>>, Map<String, Object>> parseDonMuaHangFile(String fileDonMuaHangPath) {

        Map<String, Object> stats = new HashMap<>();

        long t1 = System.currentTimeMillis();
        ExcelReader excelReader = new ExcelReader(fileDonMuaHangPath);
        Map<String, Object> output = this.scanDonMuaHangTable(excelReader, 1, "A");
        List<Map<String, Object>> records = MapUtils.getListMapStringObject(output, "records");
        List<Map<String, Object>> actualRecords = records
                .stream()
                .filter(record -> !MapUtils.getString(record, DonMuaHangHeaderMetadata.NganHangMoLc.name, "").contains("Số dòng"))
                .collect(Collectors.toList());

        this.validateFileDonMuaHang(MapUtils.ImmutableMap()
                .put("headers", MapUtils.getListMapStringObject(output, "headers"))
                .put("records", actualRecords)
                .build());

        List<Map<String, Object>> englishRecords = new ArrayList<>();
        for (Map<String, Object> record : actualRecords) {
            englishRecords.add(this.transformToEnglishFields(record));
        }

        stats.put("fileName", fileDonMuaHangPath.substring(fileDonMuaHangPath.lastIndexOf("/") + 1));
        stats.put("fileSize", NumberUtils.formatNumber1(Files.size(Path.of(fileDonMuaHangPath))));
        stats.put("recordSize", englishRecords.size());
        stats.put("parseDuration", DateTimeUtils.getRunningTimeInSecond(t1));

        return Pair.of(englishRecords, stats);
    }

    private void validateFileDonMuaHang(Map<String, Object> output) {
        List<String> headerErrorMessages = this.validateDonMuaHangHeaders(MapUtils.getListMapStringObject(output, "headers"));
        List<String> recordErrorMessages = this.validateDonMuaHangRecords(MapUtils.getListMapStringObject(output, "records"));
        boolean hasError = false;

        if (!headerErrorMessages.isEmpty()) {
            hasError = true;
            headerErrorMessages.forEach(log::error);
        }

        if (!recordErrorMessages.isEmpty()) {
            hasError = true;
            recordErrorMessages.forEach(log::error);
        }

        if (hasError) {
            throw new IllegalArgumentException(String.format("There are %s errors from HeaderValidation and %s errors from RecordValidation", headerErrorMessages.size(), recordErrorMessages.size()));
        }

    }

    private Map<String, Object> scanDonMuaHangTable(ExcelReader excelReader, int row, String column) {
        Map<String, Object> output = null;
        int currentRow = row;
        do {

            output = excelReader.readTable(column + currentRow + "");
            List<Map<String, Object>> headers = MapUtils.getListMapStringObject(output, "headers");
            List<String> errorHeaderMessages = this.validateDonMuaHangHeaders(headers);
            if (errorHeaderMessages.isEmpty()) {
                break;
            } else {
                currentRow++;
            }
        } while (currentRow - row < 10);
        return output;
    }

    private List<String> validateDonMuaHangHeaders(List<Map<String, Object>> headers) {

        List<String> messages = new ArrayList<>();

        for (DonMuaHangHeaderMetadata donMuaHangHeaderMetadata : DonMuaHangHeaderMetadata.values()) {
            boolean found = false;
            if (!donMuaHangHeaderMetadata.isOriginalField) {
                continue;
            }
            for (Map<String, Object> headerMap : headers) {
                if (StringUtils
                        .deAccent(MapUtils.getString(headerMap, "name"))
                        .equals(StringUtils.deAccent(donMuaHangHeaderMetadata.name))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                messages.add(String.format("Header named '%s' is not found", donMuaHangHeaderMetadata.name));
            }
        }

        return messages;
    }

    private List<String> validateDonMuaHangRecords(List<Map<String, Object>> records) {
        List<String> messages = new ArrayList<>();

        for (Map<String, Object> record : records) {
            Map<String, Object> deAccentedRecord = this.deAccentAllKeys(record);
            for (DonMuaHangHeaderMetadata donMuaHangHeaderMetadata : DonMuaHangHeaderMetadata.values()) {
                String val = MapUtils.getString(deAccentedRecord, donMuaHangHeaderMetadata.deAccentedName, null);
                if (ParserUtils.isNullOrEmpty(val)) {
                    if (!donMuaHangHeaderMetadata.isNullable) {
                        messages.add(String.format(
                                "%s: Value of '%s' cannot be null or empty",
                                MapUtils.getString(deAccentedRecord, DonMuaHangHeaderMetadata.SoDonHang.deAccentedName),
                                donMuaHangHeaderMetadata.deAccentedName)
                        );
                    }
                } else {
                    if (!donMuaHangHeaderMetadata.type.verifyMethod.apply(val)) {
                        messages.add(String.format(
                                "%s: Value of '%s' which is '%s' cannot be formatted as %s",
                                MapUtils.getString(deAccentedRecord, DonMuaHangHeaderMetadata.SoDonHang.deAccentedName),
                                donMuaHangHeaderMetadata.name,
                                MapUtils.getString(deAccentedRecord, donMuaHangHeaderMetadata.deAccentedName),
                                donMuaHangHeaderMetadata.type.javaType.getSimpleName())
                        );
                    }
                }
            }
        }
        return messages;
    }

    public void sendParseFileNotification(FilePurchaseOrderDTO payload, long receivedAt, MultipartFile file, Map<String, Object> stats) {
        StringBuilder msgSb = new StringBuilder();

        Map<String, Object> dmhStats = MapUtils.getMapStringObject(stats, "dmhStats");

        msgSb.append("*--- REQUEST INFORMATION ---*").append("\n");
        msgSb.append(String.format(" - File name: %s", file.getOriginalFilename())).append("\n");
        msgSb.append(String.format(" - File size: %s kb", NumberUtils.formatNumber1(file.getSize()))).append("\n");
        msgSb.append(String.format(" - Content type: %s", file.getContentType())).append("\n\n");

        msgSb.append("*--- PROCESSING STATISTIC ---*").append("\n");
        msgSb.append(" - Handle Don Mua Hang").append("\n");
        msgSb.append(String.format("      + File name: %s", MapUtils.getString(dmhStats, "fileName"))).append("\n");
        msgSb.append(String.format("      + File size: %s kb", MapUtils.getString(dmhStats, "fileSize"))).append("\n");
        msgSb.append(String.format("      + Parse duration: %s", MapUtils.getString(dmhStats, "parseDuration"))).append("\n");
        msgSb.append(String.format("      + Record size: %s", MapUtils.getString(dmhStats, "recordSize"))).append("\n\n");

        msgSb.append("*--- RESPONSE INFORMATION ---*").append("\n");
        msgSb.append(String.format(" - Duration: %s", DateTimeUtils.getRunningTimeInSecond(receivedAt))).append("\n");
        msgSb.append(String.format(" - Response body: ```%s```", JsonUtils.toJson(payload))).append("\n");
        this.slackService.sendNotification(Module.ORDER_TRACKING, SlackAuthor.LUAN_PHAN, env, String.format("Order Tracking - IMPORT - %s", DateTimeUtils.convertZonedDateTimeToFormat(DateTimeUtils.fromEpochMilli(receivedAt, "Asia/Ho_Chi_Minh"), "Asia/Ho_Chi_Minh", DateTimeUtils.FMT_02)), msgSb.toString());
    }

    private Map<String, Object> deAccentAllKeys(Map<String, Object> record) {
        Map<String, Object> deAccentedRecord = new HashMap<>();
        record.forEach((key, val) -> deAccentedRecord.put(StringUtils.makeCamelCase(key.replaceAll("đ", "d").replaceAll("Đ", "D")), val));
        return deAccentedRecord;
    }

    private Map<String, Object> transformToEnglishFields(Map<String, Object> record) {
        Map<String, Object> output = new HashMap<>();
        Arrays.stream(DonMuaHangHeaderMetadata.values())
                .filter(donMuaHangHeaderMetadata -> Objects.nonNull(donMuaHangHeaderMetadata.englishName))
                .forEach(header -> output.put(header.englishName, MapUtils.getString(record, header.name)));
        return output;
    }


}
