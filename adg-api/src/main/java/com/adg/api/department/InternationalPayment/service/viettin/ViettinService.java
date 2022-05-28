package com.adg.api.department.InternationalPayment.service.viettin;

import com.adg.api.department.InternationalPayment.service.bidv.reader.HoaDonService;
import com.adg.api.department.InternationalPayment.service.viettin.reader.ToKhaiHaiQuanService;
import com.adg.api.util.ZipUtils;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.28 22:55
 */
@Service
public class ViettinService {

    @Value("${international-payment.bidv.input.zip}")
    private String inputZip;

    @Autowired
    private ToKhaiHaiQuanService toKhaiHaiQuanService;

    @Autowired
    private HoaDonService hoaDonService;

    public Map<String, Object> readInputFile(InputStream inputStream) {
        List<File> files = new ArrayList<>();
        try {
            files = ZipUtils.uncompressZipFile(inputStream, inputZip);
            String fileHoaDon = "";
            List<String> fileTKHQ = new ArrayList<>();
            for (File f : files) {
                if (f.getName().toLowerCase().startsWith("tokhai")) {
                    fileTKHQ.add(f.getAbsolutePath());
                } else {
                    fileHoaDon = f.getAbsolutePath();
                }
            }

            List<Map<String, Object>> toKhaiHaiQuan = this.toKhaiHaiQuanService.readToKhaiHaiQuan(fileTKHQ);
            List<Map<String, Object>> hoaDon = this.hoaDonService.readHoaDonTable(fileHoaDon);

            System.out.println(JsonUtils.toJson(toKhaiHaiQuan));

            return MapUtils.ImmutableMap()
                    .put("toKhaiHaiQuan", toKhaiHaiQuan)
                    .put("hoaDon", hoaDon)
                    .build();

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            files.forEach(File::delete);
        }
        return MapUtils.ImmutableMap().build();
    }
}
