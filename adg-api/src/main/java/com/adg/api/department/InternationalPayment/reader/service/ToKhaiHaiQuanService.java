package com.adg.api.department.InternationalPayment.reader.service;

import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelReader;
import com.adg.api.department.InternationalPayment.reader.header.ToKhaiHaiQuanHeaderInfoMetadata;
import com.merlin.asset.core.utils.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.28 21:33
 */
@Service
public class ToKhaiHaiQuanService {

    @Value("${international-payment.viettin.template.to-khai-hai-quan}")
    private Resource toKhaiHaiQuanTemplate;
    private Map<String, Object> toKhaiHaiQuanAddressMap;

    @PostConstruct
    @SneakyThrows
    public void init() {
        toKhaiHaiQuanTemplate.getInputStream();
        String rawJson = new String(toKhaiHaiQuanTemplate.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        this.toKhaiHaiQuanAddressMap = JsonUtils.fromJson(rawJson, JsonUtils.TYPE_TOKEN.MAP_STRING_OBJECT.type);

    }

    @SneakyThrows
    public Pair<List<Map<String, Object>>, Map<String, Object>> parseToKhaiHaiQuanFile(List<String> toKhaiHaiQuanFilePaths) {

        List<Map<String, Object>> toKhaiHaiQuanRecords = new ArrayList<>();

        long t1 = System.currentTimeMillis();

        Map<String, Object> stats = new HashMap<>();
        List<Map<String, Object>> detailStats = new ArrayList<>();


        int i = 1;
        for (String tokhaiHaiQuanFilePath : toKhaiHaiQuanFilePaths) {
            long t2 = System.currentTimeMillis();
            ExcelReader excelReader = new ExcelReader(tokhaiHaiQuanFilePath);
            Map<String, Object> output = MapUtils.getMapStringObject(excelReader.getCellValues(this.toKhaiHaiQuanAddressMap), "data");
            List<String> tenSacThueList = MapUtils
                    .getListString(output, "Tên sắc thuế")
                    .stream()
                    .filter(tenSacThue -> !ParserUtils.isNullOrEmpty(tenSacThue.trim()))
                    .map(tenSacThue -> tenSacThue
                            .replace("N  ", "")
                            .replace("V  ",""))
                    .collect(Collectors.toList());

            List<String> tienThueList = MapUtils
                    .getListString(output, "Tổng tiền thuế")
                    .stream()
                    .filter(tienThue -> !ParserUtils.isNullOrEmpty(tienThue))
                    .map(tienThue -> tienThue
                            .replace(".", ""))
                    .collect(Collectors.toList());
            List<Map<String, Object>> chiTietThueList = new ArrayList<>();
            for (int j = 0; j < tenSacThueList.size(); j++) {
                String tenSacThue = tenSacThueList.get(j);
                String tienThue = tienThueList.get(j);
                chiTietThueList.add(MapUtils.ImmutableMap()
                        .put(ToKhaiHaiQuanHeaderInfoMetadata.TenSacThue.deAccentedName, tenSacThue)
                        .put(ToKhaiHaiQuanHeaderInfoMetadata.TienThue.deAccentedName, tienThue)
                        .build());
            }
            toKhaiHaiQuanRecords.add(MapUtils.ImmutableMap()
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.SoThuTuKhongGop.deAccentedName, i)
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.deAccentedName, ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.transformCallback.apply(output))
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.deAccentedName, ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.transformCallback.apply(output))
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.deAccentedName, ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.transformCallback.apply(output))
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.NgayDangKy.deAccentedName, ToKhaiHaiQuanHeaderInfoMetadata.NgayDangKy.transformCallback.apply(output))
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.ChiTietThue.deAccentedName, chiTietThueList)
                    .build());
            detailStats.add(MapUtils.ImmutableMap()
                    .put("fileName", tokhaiHaiQuanFilePath.substring(tokhaiHaiQuanFilePath.lastIndexOf("/") + 1))
                    .put("recordSize", chiTietThueList.size())
                    .put("fileSize", NumberUtils.formatNumber1(Files.size(Path.of(tokhaiHaiQuanFilePath))))
                    .put("parseDuration", DateTimeUtils.getRunningTimeInSecond(t2))
                    .build());
            i++;
        }

        stats.put("totalRecords", toKhaiHaiQuanRecords.size());
        stats.put("detailStats", detailStats);
        stats.put("parseDuration", DateTimeUtils.getRunningTimeInSecond(t1));

        return Pair.of(toKhaiHaiQuanRecords, stats);
    }

    public Map<String, Object> groupToKhaiHaiQuanRecordsBySoToKhai(List<Map<String, Object>> toKhaiHaiQuanRecords) {
        return toKhaiHaiQuanRecords.stream().reduce(new HashMap<>(), (result, record) -> {
            String soToKhai = MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.deAccentedName);
            List<Map<String, Object>> records = MapUtils.getListMapStringObject(result, ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.deAccentedName);
            records.add(record);
            result.put(soToKhai, records);
            return result;
        });
    }



}
