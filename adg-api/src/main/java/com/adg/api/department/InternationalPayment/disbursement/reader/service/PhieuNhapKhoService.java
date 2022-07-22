package com.adg.api.department.InternationalPayment.disbursement.reader.service;

import com.adg.api.department.InternationalPayment.disbursement.office.excel.ExcelReader;
import com.adg.api.department.InternationalPayment.disbursement.reader.header.PhieuNhapKhoHeaderMetadata;
import com.merlin.asset.core.utils.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.06.03 12:45
 */
@Service
@Log4j2
public class PhieuNhapKhoService {

    @SneakyThrows
    public Pair<List<Map<String, Object>>, Map<String, Object>> parseListPhieuNhapKho(List<String> phieuNhapKhoFilePaths) {
        List<Map<String, Object>> phieuNhapKhoRecords = new ArrayList<>();

        Map<String, Object> stats = new HashMap<>();

        List<Map<String, Object>> detailStats = new ArrayList<>();

        long t1 = System.currentTimeMillis();

        for (String phieuNhapKhoFilePath : phieuNhapKhoFilePaths) {
            long t2 = System.currentTimeMillis();
            ExcelReader excelReader = new ExcelReader(phieuNhapKhoFilePath);
            Map<String, Object> output = this. scanPhieuNhapKhoTable(excelReader, 12, "A");
            List<Map<String, Object>> records = MapUtils.getListMapStringObject(output, "records");


//            Map<String, Object> descriptionMap = this.parsePhieuNhapKhoDescription(excelReader);
            Map<String, Object> descriptionMap = this.parsePhieuNhapKhoDescription_V2(excelReader);
            int totalRecordOfEachFile = 0;
            for (Map<String, Object> record : records) {

                String sttValue = MapUtils.getString(record, PhieuNhapKhoHeaderMetadata.TenNhanHieu.name);

                if (ParserUtils.isNullOrEmpty(sttValue)) continue;
                if (sttValue.contains("Cộng")) break;

                Map<String, Object> phieuNhapKhoRecord = new HashMap<>();

                Arrays.stream(PhieuNhapKhoHeaderMetadata.values())
                        .filter(headerMetadata -> headerMetadata.isOriginalField)
                        .forEach(headerMetadata -> phieuNhapKhoRecord.put(headerMetadata.deAccentedName, MapUtils.getString(record, headerMetadata.name)));
                phieuNhapKhoRecord.put(PhieuNhapKhoHeaderMetadata.NhaCungCap.deAccentedName, MapUtils.getString(descriptionMap, PhieuNhapKhoHeaderMetadata.NhaCungCap.deAccentedName));
                phieuNhapKhoRecord.put(PhieuNhapKhoHeaderMetadata.SoHoaDon.deAccentedName, MapUtils.getString(descriptionMap, PhieuNhapKhoHeaderMetadata.SoHoaDon.deAccentedName));
                phieuNhapKhoRecord.put(PhieuNhapKhoHeaderMetadata.NgayChungTu.deAccentedName, MapUtils.getString(descriptionMap, PhieuNhapKhoHeaderMetadata.NgayChungTu.deAccentedName));
                totalRecordOfEachFile++;
                phieuNhapKhoRecords.add(phieuNhapKhoRecord);
            }

            detailStats.add(MapUtils.ImmutableMap()
                            .put("fileName", phieuNhapKhoFilePath.substring(phieuNhapKhoFilePath.lastIndexOf("/") + 1))
                            .put("recordSize", totalRecordOfEachFile)
                            .put("fileSize", NumberUtils.formatNumber1(Files.size(Path.of(phieuNhapKhoFilePath))))
                            .put("parseDuration", DateTimeUtils.getRunningTimeInSecond(t2))
                    .build());
        }
        stats.put("totalRecords", phieuNhapKhoRecords.size());
        stats.put("detailStats", detailStats);
        stats.put("parseDuration", DateTimeUtils.getRunningTimeInSecond(t1));

        return Pair.of(phieuNhapKhoRecords, stats);
    }

    private Map<String, Object> scanPhieuNhapKhoTable(ExcelReader excelReader, int row, String column) {
        Map<String, Object> output = null;
        int currentRow = row;
        do {
            output = excelReader.readTable(column + currentRow + "");
            List<Map<String, Object>> headers = MapUtils.getListMapStringObject(output, "headers");
            List<String> errorHeaderMessages = this.validatePnkHeaders(headers);
            if (errorHeaderMessages.isEmpty()) {
                break;
            } else {
                currentRow++;
            }
        } while (currentRow - row < 10);
        return output;
    }

    private List<String> validatePnkHeaders(List<Map<String, Object>> headers) {

        List<String> messages = new ArrayList<>();

        for (PhieuNhapKhoHeaderMetadata phieuNhapKhoHeaderMetadata : PhieuNhapKhoHeaderMetadata.values()) {
            boolean found = false;
            if (!phieuNhapKhoHeaderMetadata.isOriginalField) {
                continue;
            }
            for (Map<String, Object> headerMap : headers) {
                String headerName = MapUtils.getString(headerMap, "name", "");
                if (headerName.trim().isEmpty()) {
                    continue;
                }
                if (StringUtils
                        .makeCamelCase(headerName)
                        .equals(phieuNhapKhoHeaderMetadata.deAccentedName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                messages.add(String.format("Header named '%s' is not found", phieuNhapKhoHeaderMetadata.name));
            }
        }

        return messages;
    }

    public Map<String, Object> groupPhieuNhapKhoByNhaCungCapAndSoHoaDon(List<Map<String, Object>> phieuNhapKhoRecords) {
        Map<String, Object> phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon = new HashMap<>();

        for (Map<String, Object> phieuNhapKhoRecord : phieuNhapKhoRecords) {
            String nhaCungCap = MapUtils.getString(phieuNhapKhoRecord, PhieuNhapKhoHeaderMetadata.NhaCungCap.deAccentedName);
            String soHoaDon = MapUtils.getString(phieuNhapKhoRecord, PhieuNhapKhoHeaderMetadata.SoHoaDon.deAccentedName);

            Map<String, Object> phieuNhapKhoRecordsGroupBySoHoaDon = MapUtils.getMapStringObject(phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon, nhaCungCap, new HashMap<>());

            List<Map<String, Object>> phieuNhapKhoRecordsOfAHoaDon = MapUtils.getListMapStringObject(phieuNhapKhoRecordsGroupBySoHoaDon, soHoaDon, new ArrayList<>());
            phieuNhapKhoRecordsOfAHoaDon.add(phieuNhapKhoRecord);

            phieuNhapKhoRecordsGroupBySoHoaDon.put(soHoaDon, phieuNhapKhoRecordsOfAHoaDon);

            phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon.put(nhaCungCap, phieuNhapKhoRecordsGroupBySoHoaDon);
        }
        return phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon;
    }

    private Map<String, Object> parsePhieuNhapKhoDescription(ExcelReader excelReader) {

        int row = 5;
        String description;
        do {
            description = excelReader.getCellValueAsString("A" + row);
            description = ParserUtils.toString(description).replaceAll(String.valueOf((char) 160), " ");
            if (StringUtils.deAccent(description).startsWith("- Theo hoa don so")) {
                break;
            }
            row++;
        } while (row < 15);

        Map<String, Object> output = new HashMap<>();
        List<String> arr = Arrays.asList(description.split("của"));
        String ncc = arr.get(1).trim();
        String firstPart = StringUtils.deAccent(arr.get(0)).replace("- Theo hoa don so", "").trim();

        List<String> arr2 = Arrays.asList(firstPart.split(" "))
                .stream()
                .filter(str -> (
                        !str.trim().equalsIgnoreCase("ngay")
                                && !str.trim().equalsIgnoreCase("thang")
                                && !str.trim().equalsIgnoreCase("nam")
                                && !str.trim().equalsIgnoreCase("")))
                .collect(Collectors.toList());

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

    private Map<String, Object> parsePhieuNhapKhoDescription_V2(ExcelReader excelReader) {
        Map<String, Object> output = new HashMap<>();

        String sohoaDon = this.parseSoHoaDonCell(excelReader);
        String ngayChungTu = this.parseNgayChungTuCell(excelReader);
        String nhaCungCap = this.parseNccCell(excelReader);

        output.put(PhieuNhapKhoHeaderMetadata.SoHoaDon.deAccentedName, sohoaDon);
        output.put(PhieuNhapKhoHeaderMetadata.NgayChungTu.deAccentedName, ngayChungTu);
        output.put(PhieuNhapKhoHeaderMetadata.NhaCungCap.deAccentedName, nhaCungCap);

        return output;
    }

    private String parseNgayChungTuCell(ExcelReader excelReader) {
        int row = 1;
        String rawDateStr;
        do {
            rawDateStr = excelReader.getCellValueAsString("C" + row);
            rawDateStr = ParserUtils.toString(rawDateStr).replaceAll(String.valueOf((char) 160), " ");
            rawDateStr = StringUtils.deAccent(rawDateStr);
            if (StringUtils.deAccent(rawDateStr).startsWith("Ngay")) {
                break;
            }
            row++;
        } while (row < 10);

        List<String> arr2 = Arrays.asList(rawDateStr.split(" "))
                .stream()
                .filter(str -> (
                        !str.trim().equalsIgnoreCase("ngay")
                                && !str.trim().equalsIgnoreCase("thang")
                                && !str.trim().equalsIgnoreCase("nam")
                                && !str.trim().equalsIgnoreCase("")))
                .collect(Collectors.toList());

        ZonedDateTime zdt = ZonedDateTime.of(
                ParserUtils.toInt(arr2.get(2)),
                ParserUtils.toInt(arr2.get(1)),
                ParserUtils.toInt(arr2.get(0)),
                0,0,0,0, ZoneId.of("Asia/Ho_Chi_Minh")
        );

        return DateTimeUtils.convertZonedDateTimeToFormat(zdt, "Asia/Ho_Chi_Minh", DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_03));
    }

    private String parseNccCell(ExcelReader excelReader) {
        int row = 1;
        String rawDateStr;
        do {
            rawDateStr = excelReader.getCellValueAsString("A" + row);
            rawDateStr = ParserUtils.toString(rawDateStr).replaceAll(String.valueOf((char) 160), " ");
            if (StringUtils.deAccent(rawDateStr).startsWith("- Ho va ten nguoi giao:")) {
                rawDateStr = rawDateStr.split(":")[1].trim();
                break;
            }
            row++;
        } while (row < 10);

        return rawDateStr;
    }
    private String parseSoHoaDonCell(ExcelReader excelReader) {
        int row = 5;
        String rawDataStr;
        do {
            rawDataStr = excelReader.getCellValueAsString("A" + row);
            rawDataStr = ParserUtils.toString(rawDataStr).replaceAll(String.valueOf((char) 160), " ");
            rawDataStr = StringUtils.deAccent(rawDataStr);
            if (rawDataStr.startsWith("- Theo so hoa don so")) {//- Theo số hóa đơn số
                rawDataStr = rawDataStr.replace("- Theo so hoa don so", "").trim();
                break;
            }
            row++;
        } while (row < 15);
        return rawDataStr;
    }

}
