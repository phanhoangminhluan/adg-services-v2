package com.adg.api.department.InternationalPayment.controller;

import com.adg.api.department.InternationalPayment.service.bidv.reader.HoaDonService;
import com.adg.api.util.ZipUtils;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.12 02:25
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/international-payment/disbursement/bidv/")
@Log4j2
public class BidvController {

    @Autowired
    private HoaDonService hoaDonService;

    @Value("${international-payment.bidv.input.zip}")
    private String inputZip;

    @GetMapping
    public Map<String, Object> get() {
        return MapUtils.ImmutableMap()
                .put("data", MapUtils.ImmutableMap().build())
                .put("status", "ok")
                .build();
    }

    @PostMapping("import")
    @SneakyThrows
    public String importFile(@RequestParam("file") MultipartFile file) {
        List<File> files = new ArrayList<>();
        try {
            files = ZipUtils.uncompressZipFile( file.getInputStream(), inputZip);
            String fileHoaDon = "";
            List<String> filePNK = new ArrayList<>();
            for (File f : files) {
                if (f.getName().toLowerCase().startsWith("pnk")) {
                    filePNK.add(f.getAbsolutePath());
                } else {
                    fileHoaDon = f.getAbsolutePath();
                }
            }
            List<Map<String, Object>> hoaDonMap = this.hoaDonService.readHoaDonTable(fileHoaDon);
            Map<String, Object> pnkMap = this.hoaDonService.readPhieuNhapKho(filePNK);

            return JsonUtils.toJson(MapUtils.ImmutableMap()
                            .put("data", MapUtils.ImmutableMap()
                                    .put("hd", hoaDonMap)
                                    .put("pnk", this.hoaDonService.convertPnkToDTO(pnkMap))
                                    .build())
                            .put("status", "ok")
                    .build());
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            files.forEach(File::delete);
        }
        return JsonUtils.toJson(MapUtils.ImmutableMap()
                .put("data", MapUtils.ImmutableMap().build())
                .put("status", "error")
                .build());
    }

    @PostMapping(value = "export",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public byte[] exportFile(@RequestBody Map<String, Object> request) {
        try {
            log.info("Export Request: {}", JsonUtils.toJson(request));
            Map<String, Object> data = MapUtils.getMapStringObject(request, "data");
            List<Map<String, Object>> hd = MapUtils.getListMapStringObject(data, "hd");
            List<Map<String, Object>> pnk = MapUtils.getListMapStringObject(data, "pnk");
            return this.hoaDonService.exportDocuments(hd, this.hoaDonService.convertDtoToPnk(pnk));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

}
