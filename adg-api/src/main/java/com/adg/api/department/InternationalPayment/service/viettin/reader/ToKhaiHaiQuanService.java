package com.adg.api.department.InternationalPayment.service.viettin.reader;

import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelReader;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        for (String filePath : filePaths) {
            ExcelReader excelReader = new ExcelReader(filePath);
            Map<String, Object> output = MapUtils.getMapStringObject(excelReader.getCellValues(this.toKhaiHaiQuanAddressMap), "data");

            result.add(MapUtils.ImmutableMap()
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.deAccentedHeader, ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.transformCallback.apply(output))
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.deAccentedHeader, ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.transformCallback.apply(output))
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.deAccentedHeader, ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.transformCallback.apply(output))
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.NgayDangKy.deAccentedHeader, ToKhaiHaiQuanHeaderInfoMetadata.NgayDangKy.transformCallback.apply(output))
                    .build());
        }
        return result;
    }



}
