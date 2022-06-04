package com.adg.api.department.InternationalPayment.service.bidv.reader;

import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelReader;
import com.adg.api.department.InternationalPayment.service.bidv.enums.PhieuNhapKhoHeaderMetadata;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.ParserUtils;
import com.merlin.asset.core.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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

    public List<Map<String, Object>> readPhieuNhapKho(List<String> phieuNhapKhoFilePaths) {
        List<Map<String, Object>> phieuNhapKhoRecords = new ArrayList<>();

        for (String phieuNhapKhoFilePath : phieuNhapKhoFilePaths) {
            ExcelReader excelReader = new ExcelReader(phieuNhapKhoFilePath);
            Map<String, Object> output = this.scanPhieuNhapKhoTable(excelReader, 14, "A");
            List<Map<String, Object>> records = MapUtils.getListMapStringObject(output, "records");

            String rawDescription = excelReader.getCellValueAsString("A10");
            Map<String, Object> descriptionMap = this.parsePhieuNhapKhoDescription(rawDescription);

            for (Map<String, Object> record : records) {

                String sttValue = MapUtils.getString(record, PhieuNhapKhoHeaderMetadata.STT.name);

                if (ParserUtils.isNullOrEmpty(sttValue)) continue;
                if (sttValue.equals("- Tổng số tiền (Viết bằng chữ):")) break;

                Map<String, Object> phieuNhapKhoRecord = new HashMap<>();

                Arrays.stream(PhieuNhapKhoHeaderMetadata.values())
                        .filter(headerMetadata -> headerMetadata.isOriginalField)
                        .forEach(headerMetadata -> phieuNhapKhoRecord.put(headerMetadata.deAccentedName, MapUtils.getString(record, headerMetadata.name)));
                phieuNhapKhoRecord.put(PhieuNhapKhoHeaderMetadata.NhaCungCap.deAccentedName, MapUtils.getString(descriptionMap, PhieuNhapKhoHeaderMetadata.NhaCungCap.deAccentedName));
                phieuNhapKhoRecord.put(PhieuNhapKhoHeaderMetadata.SoHoaDon.deAccentedName, MapUtils.getString(descriptionMap, PhieuNhapKhoHeaderMetadata.SoHoaDon.deAccentedName));

                phieuNhapKhoRecords.add(phieuNhapKhoRecord);
            }
        }
        return phieuNhapKhoRecords;
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

}
