package com.adg.api.department.InternationalPayment.bank.mb.writer;

import com.adg.api.department.InternationalPayment.NhaCungCapDTO;
import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderInfo;
import com.adg.api.department.InternationalPayment.reader.header.PhieuNhapKhoHeaderMetadata;
import com.merlin.asset.core.utils.MapUtils;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.06.19 23:25
 */
public enum DanhSachHoaDonCamHangHeaderInfoMetadata implements AdgExcelTableHeaderInfo {

    HSV(
          "HSV",
            "A3",
            CellType.STRING,
            pnkRecord -> "",
            true

    ),
    SoHoaDon(
        "SỐ HÓA ĐƠN",
            "B3",
            CellType.NUMERIC,
            pnkRecord -> MapUtils.getString(pnkRecord, PhieuNhapKhoHeaderMetadata.SoHoaDon.deAccentedName),
            false
    ),
    NgayHoaDon(
            "NGÀY HÓA ĐƠN",
            "C3",
            CellType.STRING,
            pnkRecord -> MapUtils.getString(pnkRecord, PhieuNhapKhoHeaderMetadata.NgayChungTu.deAccentedName),
            true

    ),
    DonGia(
            "ĐƠN GIÁ",
            "D3",
            CellType.NUMERIC,
            pnkRecord -> MapUtils.getString(pnkRecord, PhieuNhapKhoHeaderMetadata.DonGia.deAccentedName),
            false

    ),
    SoLuongHangHoaDon(
    "SỐ LƯỢNG HÀNG HÓA ĐƠN",
            "E3",
            CellType.NUMERIC,
            pnkRecord -> MapUtils.getString(pnkRecord, PhieuNhapKhoHeaderMetadata.SoLuongTheoChungTu.deAccentedName),
            false
    ),
    SoTienHoaDon(
            "SỐ TIỀN HÓA ĐƠN",
            "F3",
            CellType.NUMERIC,
            pnkRecord -> MapUtils.getString(pnkRecord, PhieuNhapKhoHeaderMetadata.ThanhTien.deAccentedName),
            false
    ),
    NhaCungCap(
            "NCC",
            "G3",
            CellType.STRING,
            pnkRecord -> {
                String nccRaw = MapUtils.getString(pnkRecord, PhieuNhapKhoHeaderMetadata.NhaCungCap.deAccentedName);
                NhaCungCapDTO nhaCungCapDTO = NhaCungCapDTO.nhaCungCapMap.get(nccRaw);
                if (nhaCungCapDTO == null) {
                    return nccRaw;
                }
                return nhaCungCapDTO.getShortName();
            },
            true
    ),
    MaHang(
            "MÃ HÀNG",
            "H3",
            CellType.STRING,
            pnkRecord -> MapUtils.getString(pnkRecord, PhieuNhapKhoHeaderMetadata.MaSo.deAccentedName),
            false
    ),
    SoLuongCamHang(
            "Số lượng cầm hàng",
            "I3",
            CellType.NUMERIC,
            pnkRecord -> MapUtils.getString(pnkRecord, PhieuNhapKhoHeaderMetadata.SoLuongTheoChungTu.deAccentedName),
            false
    ),
    SoTienGn(
            "SỐ TIỀN GN",
            "J3",
            CellType.NUMERIC,
            pnkRecord -> MapUtils.getString(pnkRecord, PhieuNhapKhoHeaderMetadata.ThanhTien.deAccentedName),
            false
    )
    ;
    private final String header;
    private final String cellAddress;
    private final CellType cellType;
    public final Function<Map<String, Object>, String> transformCallback;;
    public final boolean isGroupedColumn;

    DanhSachHoaDonCamHangHeaderInfoMetadata(String header, String cellAddress, CellType cellType, Function<Map<String, Object>, String> transformCallback, boolean isGroupedColumn) {
        this.header = header;
        this.cellAddress = cellAddress;
        this.cellType = cellType;
        this.transformCallback = transformCallback;
        this.isGroupedColumn = isGroupedColumn;
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
        return this.isGroupedColumn;
    }
}
