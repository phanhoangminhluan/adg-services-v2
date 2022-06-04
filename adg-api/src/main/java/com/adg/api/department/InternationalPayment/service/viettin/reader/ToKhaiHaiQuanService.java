package com.adg.api.department.InternationalPayment.service.viettin.reader;

import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelReader;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.ParserUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    public List<Map<String, Object>> readToKhaiHaiQuan(List<String> filePaths) {

        List<Map<String, Object>> result = new ArrayList<>();

        int i = 1;
        for (String filePath : filePaths) {
            ExcelReader excelReader = new ExcelReader(filePath);
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
            result.add(MapUtils.ImmutableMap()
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.SoThuTuKhongGop.deAccentedName, i)
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.deAccentedName, ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.transformCallback.apply(output))
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.deAccentedName, ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.transformCallback.apply(output))
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.deAccentedName, ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.transformCallback.apply(output))
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.NgayDangKy.deAccentedName, ToKhaiHaiQuanHeaderInfoMetadata.NgayDangKy.transformCallback.apply(output))
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.ChiTietThue.deAccentedName, chiTietThueList)
                    .build());
            i++;
        }
        return result;
    }



}
