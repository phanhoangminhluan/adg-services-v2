package com.adg.api.department.InternationalPayment.service.bidv.enums;

import com.merlin.asset.core.utils.StringUtils;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.12 00:11
 */
public enum PhieuNhapKhoHeaderMetadata {

    STT("A", false, true, AdgHeaderType.INTEGER),
    TenNhanHieu("B", false, true, AdgHeaderType.STRING),
    MaSo("C", false, true, AdgHeaderType.STRING),
    DonViTinh("D", false, true, AdgHeaderType.STRING),
    SoLuongTheoChungTu("1", false, true, AdgHeaderType.INTEGER),
    SoLuongThucNhap("2", false, true, AdgHeaderType.INTEGER),
    DonGia("3", false, true, AdgHeaderType.DOUBLE),
    ThanhTien("4", false, true, AdgHeaderType.DOUBLE),
    SoHoaDon(HoaDonHeaderMetadata.SoHoaDon.name, false, false, AdgHeaderType.STRING),
    NgayChungTu(HoaDonHeaderMetadata.NgayChungTu.name, false, false, AdgHeaderType.STRING),
    NhaCungCap(HoaDonHeaderMetadata.NhaCungCap.name, false, false, AdgHeaderType.STRING),
    HangHoa("Hàng hoá", false, false, AdgHeaderType.STRING),
    ;
    public final String name;
    public final String deAccentedName;
    public final boolean isNullable;
    public final boolean isOriginalField;
    public final AdgHeaderType type;

    PhieuNhapKhoHeaderMetadata(String name, boolean isNullable, boolean isOriginalField, AdgHeaderType type) {
        this.name = name;
        this.deAccentedName = StringUtils.makeCamelCase(this.name);
        this.isNullable = isNullable;
        this.isOriginalField = isOriginalField;
        this.type = type;
    }
}
