package com.adg.api.department.InternationalPayment.service.bidv;

import com.google.common.reflect.TypeToken;
import com.merlin.asset.core.utils.FileUtils;
import com.merlin.asset.core.utils.JsonUtils;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.11 14:07
 */
@Data
@Builder
@Getter
public class NhaCungCapDTO {

    public static Map<String, NhaCungCapDTO> nhaCungCapMap = new HashMap<>();

    static {
        String raw = FileUtils.readContent("/home/ubuntu/adg-services-v2/adg-api/src/main/resources/adg-clients.json");
        List<NhaCungCapDTO> dtos = JsonUtils.fromJson(raw, new TypeToken<List<NhaCungCapDTO>>(){}.getType());
        for (NhaCungCapDTO dto : dtos) {
            nhaCungCapMap.put(dto.tenKhachHang, dto);
        }
    }

    private String maKhachHang;
    private String tenKhachHang;
    private String soTaiKhoan;
    private String tenNganHang;

    public static void main(String[] args) {
        String raw = FileUtils.readContent("./adg-api/src/main/resources/adg-clients.json");
        System.out.println(raw);
    }
}
