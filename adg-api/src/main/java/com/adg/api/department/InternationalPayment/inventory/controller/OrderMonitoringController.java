package com.adg.api.department.InternationalPayment.inventory.controller;

import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
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
public class OrderMonitoringController {

    @PostMapping("order/import")
    @SneakyThrows
    private Map<String, Object> importFile(@RequestParam("file") MultipartFile file) {
        return MapUtils.ImmutableMap()
                .put("data", List.of(
                        MapUtils.ImmutableMap()
                                .put("nganHangMoLc", "NH TCB")
                                .put("soDonHang", "ADG/IRPC-021/22")
                                .put("maNhaCungCap", "HN.IRPC")
                                .put("maHang", "PP-TH 1102K")
                                .put("soLuongDatHang", 82500)
                                .put("soLuongDaNhan", 82500)
                                .put("soLuongConLai", 0)
                                .put("donGia", 1.32)
                                .put("giaTriDatHang", 108900.0)
                                .put("ngayMoLc", "18/05/2022")
                                .put("tinhTrang", "Hoàn thành")
                                .put("duKienHangVe", "03/06/2022")
                                .put("dienGiai", "TCB - TF2213801034/CLN")
                                .put("khoDangKy", "")
                                .put("hanThanhToan", "")
                                .put("daThanhToan", "")
                                .put("hopDongMua", "")
                                .put("hanLuuCont", "")
                                .build(),
                        MapUtils.ImmutableMap()
                                .put("nganHangMoLc", "NH TCB")
                                .put("soDonHang", "ADG/IRPC-021/22")
                                .put("maNhaCungCap", "HN.IRPC")
                                .put("maHang", "PP-TH 1100NK")
                                .put("soLuongDatHang", 33000)
                                .put("soLuongDaNhan", 33000)
                                .put("soLuongConLai", 0)
                                .put("donGia", 1.32)
                                .put("giaTriDatHang", 43560.0)
                                .put("ngayMoLc", "18/05/2022")
                                .put("tinhTrang", "Hoàn thành")
                                .put("duKienHangVe", "03/06/2022")
                                .put("dienGiai", "TCB - TF2213801034/CLN")
                                .put("khoDangKy", "")
                                .put("hanThanhToan", "")
                                .put("daThanhToan", "")
                                .put("hopDongMua", "")
                                .put("hanLuuCont", "")
                                .build(),
                        MapUtils.ImmutableMap()
                                .put("nganHangMoLc", "NH TCB")
                                .put("soDonHang", "ADG/IRPC-021/22")
                                .put("maNhaCungCap", "HN.IRPC")
                                .put("maHang", "PP-TH 1126NK")
                                .put("soLuongDatHang", 49500)
                                .put("soLuongDaNhan", 49500)
                                .put("soLuongConLai", 0)
                                .put("donGia", 1.35)
                                .put("giaTriDatHang", 43560.0)
                                .put("ngayMoLc", "18/05/2022")
                                .put("tinhTrang", "Hoàn thành")
                                .put("duKienHangVe", "03/06/2022")
                                .put("dienGiai", "TCB - TF2213801034/CLN")
                                .put("khoDangKy", "")
                                .put("hanThanhToan", "")
                                .put("daThanhToan", "")
                                .put("hopDongMua", "")
                                .put("hanLuuCont", "")
                                .build()
                ))
                .build();
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
    private Map<String, Object> importFile(@RequestParam("pageIndex") int pageIndex, @RequestParam("pageSize") int pageSize) {
        return MapUtils.ImmutableMap()
                .put("data", MapUtils.ImmutableMap()
                        .put("inventory", List.of(
                                MapUtils.ImmutableMap()
                                        .put("id", "21748af2-ff27-11ec-b939-0242ac120002")
                                        .put("contractId", "UP-TF2035301047/CLN")
                                        .put("productId", "ABS3 750SW")
                                        .put("price", 2430)
                                        .put("totalStockQuantity", 400)
                                        .put("totalReleaseQuantity", 100)
                                        .put("lcDate", "2021-02-16")
                                        .build(),
                                MapUtils.ImmutableMap()
                                        .put("id", "79c68c94-f125-4c2f-beec-689fa1088c16")
                                        .put("contractId", "UP-TF3035301047/CLN")
                                        .put("productId", "ABS2 750SW")
                                        .put("price", 1255)
                                        .put("totalStockQuantity", 3000)
                                        .put("totalReleaseQuantity", 200)
                                        .put("lcDate", "2021-02-16")
                                        .build()))
                        .put("pageIndex", 1)
                        .put("pageSize", 2)
                        .put("totalRecords", 2)
                        .build())
                .put("status", "ok")
                .put("message", "ok")
                .build();
    }

}
