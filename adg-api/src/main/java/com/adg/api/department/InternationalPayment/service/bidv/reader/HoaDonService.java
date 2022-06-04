package com.adg.api.department.InternationalPayment.service.bidv.reader;

import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelReader;
import com.adg.api.department.InternationalPayment.service.bidv.enums.HoaDonHeaderMetadata;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.ParserUtils;
import com.merlin.asset.core.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.10 21:47
 */
@Service
@Log4j2
public class HoaDonService {

    private static final Logger logger = LoggerFactory.getLogger(HoaDonService.class);

    public List<Map<String, Object>> parseHoaDonFile(String fileHoaDonPath) {
        ExcelReader excelReader = new ExcelReader(fileHoaDonPath);
        Map<String, Object> output = excelReader.readTable("A2");
        List<Map<String, Object>> records = MapUtils.getListMapStringObject(output, "records");
        List<Map<String, Object>> actualRecords = records.stream().filter(record -> !ParserUtils.isNullOrEmpty(MapUtils.getString(record, HoaDonHeaderMetadata.SoChungTu.name, "").trim())).collect(Collectors.toList());

        this.validateFileHoaDon(MapUtils.ImmutableMap()
                .put("headers", MapUtils.getListMapStringObject(output, "headers"))
                .put("records", actualRecords)
                .build());

        List<Map<String, Object>> deAccentedRecords = new ArrayList<>();
        for (Map<String, Object> record : actualRecords) {
            deAccentedRecords.add(this.deAccentAllKeys(record));
        }

        return deAccentedRecords;
    }

    public Pair<Map<String, Object>, List<Map<String, Object>>> transformHoaDonRecords(List<Map<String, Object>> hoaDonRecords) {
        Map<String, Object> hoaDonRecordsGroupByNhaCungCap = new HashMap<>();
        List<Map<String, Object>> hoaDonRecordsSortBySttKhongGop = new ArrayList<>();
        int sttGop = 0;
        int sttKhongGop = 1;
        for (Map<String, Object> hoaDonRecord : hoaDonRecords) {
            String nhaCungCap = MapUtils.getString(hoaDonRecord, HoaDonHeaderMetadata.NhaCungCap.deAccentedName);
            String soHoaDon = MapUtils.getString(hoaDonRecord, HoaDonHeaderMetadata.SoHoaDon.deAccentedName);
            Map<String, Object> hoaDonMapGroupByNhaCungCap = MapUtils.getMapStringObject(hoaDonRecordsGroupByNhaCungCap, nhaCungCap, new HashMap<>());
            Map<String, Object> hoaDonDetail = MapUtils.getMapStringObject(hoaDonMapGroupByNhaCungCap, soHoaDon, new HashMap<>());

            if (hoaDonMapGroupByNhaCungCap.isEmpty()) {
                sttGop++;
            }

            for (HoaDonHeaderMetadata hoaDonHeaderMetadata : HoaDonHeaderMetadata.values()) {
                if (hoaDonHeaderMetadata.isOriginalField) {
                    hoaDonDetail.put(hoaDonHeaderMetadata.deAccentedName, MapUtils.getString(hoaDonRecord, hoaDonHeaderMetadata.deAccentedName));
                }
            }

            hoaDonDetail.put(HoaDonHeaderMetadata.SoThuTuKhongGop.deAccentedName, sttKhongGop);

            hoaDonRecordsSortBySttKhongGop.add(hoaDonDetail);

            List<String> listSoHoaDon = MapUtils.getListString(hoaDonMapGroupByNhaCungCap, HoaDonHeaderMetadata.ListSoHoaDon.deAccentedName, new ArrayList<>());
            listSoHoaDon.add(soHoaDon);

            double tongSoTienThanhToanCacHoaDon = MapUtils.getDouble(hoaDonMapGroupByNhaCungCap, HoaDonHeaderMetadata.TongTienThanhToanCacHoaDon.deAccentedName, 0);
            tongSoTienThanhToanCacHoaDon += MapUtils.getDouble(hoaDonDetail, HoaDonHeaderMetadata.TongTienThanhToan.deAccentedName);


            hoaDonMapGroupByNhaCungCap.put(soHoaDon, hoaDonDetail);
            hoaDonMapGroupByNhaCungCap.put(HoaDonHeaderMetadata.ListSoHoaDon.deAccentedName, listSoHoaDon);
            hoaDonMapGroupByNhaCungCap.put(HoaDonHeaderMetadata.TongTienThanhToanCacHoaDon.deAccentedName, tongSoTienThanhToanCacHoaDon);
            hoaDonMapGroupByNhaCungCap.put(HoaDonHeaderMetadata.SoThuTuCoGop.deAccentedName, sttGop);
            hoaDonMapGroupByNhaCungCap.put(HoaDonHeaderMetadata.NhaCungCap.deAccentedName, nhaCungCap);

            hoaDonRecordsGroupByNhaCungCap.put(nhaCungCap, hoaDonMapGroupByNhaCungCap);

            sttKhongGop++;
        }

        hoaDonRecordsSortBySttKhongGop.sort(Comparator.comparingInt(m -> MapUtils.getInt(m, HoaDonHeaderMetadata.SoThuTuKhongGop.deAccentedName)));

        return Pair.of(hoaDonRecordsGroupByNhaCungCap, hoaDonRecordsSortBySttKhongGop);
    }


    private void validateFileHoaDon(Map<String, Object> output) {
        List<String> headerErrorMessages = this.validateHoaDonHeaders(MapUtils.getListMapStringObject(output, "headers"));
        List<String> recordErrorMessages = this.validateHoaDonRecords(MapUtils.getListMapStringObject(output, "records"));
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

    private List<String> validateHoaDonHeaders(List<Map<String, Object>> headers) {

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

    private List<String> validateHoaDonRecords(List<Map<String, Object>> records) {
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

    public static String transformSoHoaDon(String soHoaDon) {
        while (soHoaDon.startsWith("0")) {
            soHoaDon = soHoaDon.substring(1);
        }
        return "0" + soHoaDon;
    }

}
