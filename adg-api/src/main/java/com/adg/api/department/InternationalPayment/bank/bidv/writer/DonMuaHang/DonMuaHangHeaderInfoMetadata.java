package com.adg.api.department.InternationalPayment.bank.bidv.writer.DonMuaHang;

import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderInfo;
import com.adg.api.department.InternationalPayment.reader.header.PhieuNhapKhoHeaderMetadata;
import com.merlin.asset.core.utils.MapUtils;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.03 12:24
 */
public enum DonMuaHangHeaderInfoMetadata implements AdgExcelTableHeaderInfo {
    MaHang(
            "Mã hàng",
            "A14",
            CellType.STRING,
            record -> MapUtils.getString(record, PhieuNhapKhoHeaderMetadata.MaSo.deAccentedName)
    ),
    DienGiai(
            "Diễn giải",
            "B14",
            CellType.STRING,
            record -> MapUtils.getString(record, PhieuNhapKhoHeaderMetadata.TenNhanHieu.deAccentedName)
    ),
    DonVi(
            "Đơn vị",
            "C14",
            CellType.STRING,
            record -> MapUtils.getString(record, PhieuNhapKhoHeaderMetadata.DonViTinh.deAccentedName)
    ),
    SoLuong(
            "Số lượng",
            "D14",
            CellType.NUMERIC,
            record -> MapUtils.getString(record, PhieuNhapKhoHeaderMetadata.SoLuongTheoChungTu.deAccentedName)
    ),
    DonGia(
            "Đơn giá",
            "E14",
            CellType.NUMERIC,
            record -> MapUtils.getString(record, PhieuNhapKhoHeaderMetadata.DonGia.deAccentedName)
    ),
    ThanhTien(
            "Thành tiền",
            "F14",
            CellType.NUMERIC,
            record -> MapUtils.getString(record, PhieuNhapKhoHeaderMetadata.ThanhTien.deAccentedName)
    ),
    ;

    private final String header;
    private final String cellAddress;
    private final CellType cellType;
    public final Function<Map<String, Object>, String> transformCallback;

    DonMuaHangHeaderInfoMetadata(String header, String cellAddress, CellType cellType, Function<Map<String, Object>, String> transformCallback) {
        this.header = header;
        this.cellAddress = cellAddress;
        this.cellType = cellType;
        this.transformCallback = transformCallback;
    }

    @Override
    public String getHeaderName() {
        return this.header;
    }

    @Override
    public String getCellAddress() {
        return this.cellAddress;
    }

    @Override
    public int getOrdinal() {
        return this.ordinal();
    }

    @Override
    public CellType getCellType() {
        return this.cellType;
    }

    @Override
    public boolean isGroupedColumn() {
        return false;
    }
}
