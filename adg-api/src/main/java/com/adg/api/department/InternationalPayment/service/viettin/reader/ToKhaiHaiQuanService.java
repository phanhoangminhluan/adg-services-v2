package com.adg.api.department.InternationalPayment.service.viettin.reader;

import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelReader;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
        this.toKhaiHaiQuanAddressMap = JsonUtils.fromJson(FileUtils.readFileToString(toKhaiHaiQuanTemplate.getFile()), JsonUtils.TYPE_TOKEN.MAP_STRING_OBJECT.type);

    }
//
//    @SneakyThrows
//    public static void main(String[] args) {
//        String val = FileUtils.readFileToString(new File(ExcelReader.TO_KHAI_HAI_QUAN));
//
//        Map<String, Object> addressMap = JsonUtils.fromJson(val, JsonUtils.TYPE_TOKEN.MAP_STRING_OBJECT.type);
//
//        ExcelReader excelReader = new ExcelReader("/Users/luan.phm/engineering/Projects/ADongGroup/adg-services-v2/adg-api/src/main/resources/viettin/ToKhaiHQ7N_104653931540.xls");
//        Map<String, Object> result = excelReader.getCellValues(addressMap);
//        System.out.println(JsonUtils.toJson(result));
//    }


    public List<Map<String, Object>> readToKhaiHaiQuan(List<String> filePaths) {

        List<Map<String, Object>> result = new ArrayList<>();

        for (String filePath : filePaths) {
            ExcelReader excelReader = new ExcelReader(filePath);
            Map<String, Object> output = MapUtils.getMapStringObject(excelReader.getCellValues(this.toKhaiHaiQuanAddressMap), "data");

            String val = ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.transformCallback.apply(output);
            String val2 = ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.transformCallback.apply(output);
            String val3 = ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.transformCallback.apply(output);
            result.add(MapUtils.ImmutableMap()
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.deAccentedHeader, ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.transformCallback.apply(output))
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.deAccentedHeader, ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.transformCallback.apply(output))
                            .put(ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.deAccentedHeader, ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.transformCallback.apply(output))
                    .build());
        }
        return result;
    }



}
