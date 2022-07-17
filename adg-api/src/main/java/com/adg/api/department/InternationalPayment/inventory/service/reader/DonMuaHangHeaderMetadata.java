package com.adg.api.department.InternationalPayment.inventory.service.reader;

import com.adg.api.department.InternationalPayment.disbursement.enums.AdgHeaderType;
import com.merlin.asset.core.utils.StringUtils;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 00:34
 */
public enum DonMuaHangHeaderMetadata {
    NganHangMoLc("Ngân hàng mở LC", null, false, true, AdgHeaderType.STRING),
    SoDonHang("Số đơn hàng", "orderCode", false, true, AdgHeaderType.STRING),
    MaNhaCungCap("Mã nhà cung cấp",  "providerCode", false, true, AdgHeaderType.STRING),
    MaHang("Mã hàng",  "productId", false, true, AdgHeaderType.STRING),
    SoLuongDatHang("Số lượng đặt hàng", "orderQuantity", false, true, AdgHeaderType.INTEGER),
    SoLuongDaNhan("Số lượng đã nhận",  "receivedQuantity", false, true, AdgHeaderType.INTEGER),
    SoLuongConLai("Số lượng còn lại", null, false, true, AdgHeaderType.INTEGER),
    DonGia("Đơn giá",  "unitPrice", false, true, AdgHeaderType.DOUBLE),
    GiaTriDatHang("Giá trị đặt hàng", null, false, true, AdgHeaderType.DOUBLE),
    NgayMoLc("Ngày mở LC", "lcDate", false, true, AdgHeaderType.DATE),
    TinhTrang("Tình trạng",  "status", false, true, AdgHeaderType.STRING),
    DuKienHangVe("Dự kiến hàng về",  null,false, true, AdgHeaderType.DATE),
    DienGiai("Diễn giải", "description", false, true, AdgHeaderType.STRING),
    KhoDangKy("Kho đăng ký", null,true, true, AdgHeaderType.STRING),
    HanThanhToan("Hạn thanh toán", null,true, true, AdgHeaderType.STRING),
    DaThanhToan("Đã thanh toán", null,true, true, AdgHeaderType.STRING),
    HopDongMua("Hợp đồng mua", null,true, true, AdgHeaderType.STRING),
    HanLuuCont("Hạn lưu cont", null,true, true, AdgHeaderType.STRING)
    ;
    public final String name;
    public final String deAccentedName;
    public final boolean isNullable;
    public final boolean isOriginalField;
    public final AdgHeaderType type;
    public final String englishName;

    DonMuaHangHeaderMetadata(String name, String englishName, boolean isNullable, boolean isOriginalField, AdgHeaderType type) {
        this.name = name;
        this.englishName = englishName;
        this.deAccentedName = StringUtils.makeCamelCase(this.name).replaceAll("Đ", "D").replaceAll("đ", "d");
        this.isNullable = isNullable;
        this.isOriginalField = isOriginalField;
        this.type = type;
    }

}
