package com.adg.api.department.InternationalPayment.disbursement.reader.header;

import com.adg.api.department.InternationalPayment.disbursement.enums.AdgHeaderType;
import com.merlin.asset.core.utils.StringUtils;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.10 22:46
 */
public enum HoaDonHeaderMetadata {
    STT("STT", false, true, AdgHeaderType.STRING),
    NgayChungTu("Ngày hạch toán", false, true, AdgHeaderType.DATE),
    SoChungTu("Số chứng từ", false, true, AdgHeaderType.STRING),
    SoHoaDon("Số hoá đơn", false, true, AdgHeaderType.STRING),
    NhaCungCap("Nhà cung cấp", false, true, AdgHeaderType.STRING),
    DienGiai("Diễn giải", false, true, AdgHeaderType.STRING),
    TongTienThanhToan("Tổng tiền thanh toán", false, true, AdgHeaderType.DOUBLE),
    ChiPhiMuaHang("Chi phí mua hàng", false, true, AdgHeaderType.DOUBLE),
    GiaTriNhapKho("Giá trị nhập kho", false, true, AdgHeaderType.DOUBLE),
    TTNhanHoaDon("TT nhận hóa đơn", false, true, AdgHeaderType.STRING),
    SoThuTuKhongGop("Số thứ tự không gộp", true, false, AdgHeaderType.INTEGER),
    SoThuTuCoGop("Số thứ tự có gộp", true, false, AdgHeaderType.INTEGER),
    ListSoHoaDon("List số hoá đơn", true, false, AdgHeaderType.INTEGER),
    TongTienThanhToanCacHoaDon("Tổng tiền thanh toán các hoá đơn", true, false, AdgHeaderType.INTEGER),
    ;
    public final String name;
    public final String deAccentedName;
    public final boolean isNullable;
    public final boolean isOriginalField;
    public final AdgHeaderType type;

    HoaDonHeaderMetadata(String name, boolean isNullable, boolean isOriginalField, AdgHeaderType type) {
        this.name = name;
        this.deAccentedName = StringUtils.makeCamelCase(this.name);
        this.isNullable = isNullable;
        this.isOriginalField = isOriginalField;
        this.type = type;
    }
}
