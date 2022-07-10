package com.adg.api.department.InternationalPayment.inventory.service.reader;

import com.adg.api.department.InternationalPayment.disbursement.enums.AdgHeaderType;
import com.merlin.asset.core.utils.StringUtils;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 00:34
 */
public enum DonMuaHangHeaderMetadata {
    NganHangMoLc("Ngân hàng mở LC", false, true, AdgHeaderType.STRING),
    SoDonHang("Số đơn hàng", false, true, AdgHeaderType.STRING),
    MaNhaCungCap("Mã nhà cung cấp", false, true, AdgHeaderType.STRING),
    MaHang("Mã hàng", false, true, AdgHeaderType.STRING),
    SoLuongDatHang("Số lượng đặt hàng", false, true, AdgHeaderType.INTEGER),
    SoLuongDaNhan("Số lượng đã nhận", false, true, AdgHeaderType.INTEGER),
    SoLuongConLai("Số lượng còn lại", false, true, AdgHeaderType.INTEGER),
    DonGia("Đơn giá", false, true, AdgHeaderType.DOUBLE),
    GiaTriDatHang("Giá trị đặt hàng", false, true, AdgHeaderType.DOUBLE),
    NgayMoLc("Ngày mở LC", false, true, AdgHeaderType.DATE),
    TinhTrang("Tình trạng", false, true, AdgHeaderType.STRING),
    DuKienHangVe("Dự kiến hàng về", false, true, AdgHeaderType.DATE),
    DienGiai("Diễn giải", false, true, AdgHeaderType.STRING),
    KhoDangKy("Kho đăng ký", true, true, AdgHeaderType.STRING),
    HanThanhToan("Hạn thanh toán", true, true, AdgHeaderType.STRING),
    DaThanhToan("Đã thanh toán", true, true, AdgHeaderType.STRING),
    HopDongMua("Hợp đồng mua", true, true, AdgHeaderType.STRING),
    HanLuuCont("Hạn lưu cont", true, true, AdgHeaderType.STRING)
    ;
    public final String name;
    public final String deAccentedName;
    public final boolean isNullable;
    public final boolean isOriginalField;
    public final AdgHeaderType type;

    DonMuaHangHeaderMetadata(String name, boolean isNullable, boolean isOriginalField, AdgHeaderType type) {
        this.name = name;
        this.deAccentedName = StringUtils.makeCamelCase(this.name).replaceAll("Đ", "D").replaceAll("đ", "d");
        this.isNullable = isNullable;
        this.isOriginalField = isOriginalField;
        this.type = type;
    }

}
