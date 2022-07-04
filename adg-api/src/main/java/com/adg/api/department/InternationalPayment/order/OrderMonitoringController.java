package com.adg.api.department.InternationalPayment.order;

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
