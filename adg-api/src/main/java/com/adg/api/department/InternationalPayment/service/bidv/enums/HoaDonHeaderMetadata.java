package com.adg.api.department.InternationalPayment.service.bidv.enums;

import com.merlin.asset.core.utils.StringUtils;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.10 22:46
 */
public enum HoaDonHeaderMetadata {
    NgayHachToan("Ngày hạch toán", false, true, AdgHeaderType.DATE),
    SoChungTu("Số chứng từ", false, true, AdgHeaderType.STRING),
    NgayChungTu("Ngày chứng từ", false, true, AdgHeaderType.DATE),
    SoHoaDon("Số hoá đơn", false, true, AdgHeaderType.STRING),
    NhaCungCap("Nhà cung cấp", false, true, AdgHeaderType.STRING),
    DienGiai("Diễn giải", false, true, AdgHeaderType.STRING),
    TongTienHang("Tổng tiền hàng", false, true, AdgHeaderType.DOUBLE),
    TienChietKhau("Tiền chiết khấu", false, true, AdgHeaderType.DOUBLE),
    TienThueGTGT("Tiền thuế GTGT", false, true, AdgHeaderType.DOUBLE),
    TongTienThanhToan("Tổng tiền thanh toán", false, true, AdgHeaderType.DOUBLE),
    ChiPhiMuaHang("Chi phí mua hàng", false, true, AdgHeaderType.DOUBLE),
    GiaTriNhapKho("Giá trị nhập kho", false, true, AdgHeaderType.DOUBLE),
    NhanHoaDon("Nhận hoá đơn", false, true, AdgHeaderType.STRING),
    LaChiPhiMuaHang("Là chi phí mua hàng", false, true, AdgHeaderType.BOOLEAN),
    LoaiChungTu("Loại chứng từ", false, true, AdgHeaderType.STRING),
    PhiTruocHaiQuan("Phí trước hải quan", false, true, AdgHeaderType.DOUBLE),
    TienThueNK("Tiền thuế NK", false, true, AdgHeaderType.DOUBLE),
    TienThueTTDB("Tiền thuế TTĐB", false, true, AdgHeaderType.DOUBLE),
    SoChungTuSoQT("Số chứng từ (Sổ QT)", true, true, AdgHeaderType.DOUBLE),
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
