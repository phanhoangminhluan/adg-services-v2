package com.adg.api.department.InternationalPayment.inventory.controller;

import com.adg.api.department.InternationalPayment.inventory.service.reader.DonMuaHangService;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.04 06:54
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/international-payment/tracking/tcb/")
@Log4j2
public class OrderTrackingController {

    @Autowired
    private DonMuaHangService donMuaHangService;

    @PostMapping("order/import")
    @SneakyThrows
    private Map<String, Object> importFile(@RequestParam("file") MultipartFile file) {

        long t1 = System.currentTimeMillis();

        log.info("Import request. Original File Name: {}. Size: {}. Content Type: {}", file.getOriginalFilename(), file.getSize(), file.getContentType());

        Pair<Map<String, Object>, Map<String, Object>> pair = this.donMuaHangService.parseFile(file.getInputStream());

        Map<String, Object> payload = MapUtils.ImmutableMap().put("data", pair.getFirst()).put("status", "ok").build();

        log.info(String.format("Import response. %s", JsonUtils.toJson(payload)));

        this.donMuaHangService.sendParseFileNotification(payload, t1, file, pair.getSecond());

        return payload;

    }

    @PostMapping("order/confirm")
    @SneakyThrows
    private Map<String, Object> previewFile(@RequestBody Map<String, Object> request) {
        return MapUtils.ImmutableMap()
                .put("data", List.of())
                .put("status", "ok")
                .put("message", "ok")
                .build();
    }

    @GetMapping("inventory")
    @SneakyThrows
    private List<Map<String, Object>> importFile(@RequestParam("port") String port) {
        return List.of(
                MapUtils.ImmutableMap()
                        .put("soKheUoc", "UP-TF2213901116/CLN-02")
                        .put("matHang", "PP H5300")
                        .put("ton", "")
                        .put("nhap", 330)
                        .put("soLuongDaGiaiChap", 22)
                        .put("xuatBan", 22)
                        .put("soLuongDaGiaiChapChuaBan", "")
                        .put("tonChuaBan", 308)
                        .put("tonChuaGiaiChap", 308)
                        .put("ngayLayChungTu", "07/06/2022")
                        .put("ngayDenHan", "22/06/2022")
                        .put("donGiaUsdTan", 1265)
                        .put("triGiaUSD", 389620)
                        .put("triGiaVND",  9156070000.0)
                        .put("soTienNoCang",  7324856000.0)
                        .put("ghiChu", "NT")
                        .build()
        );
    }

}
