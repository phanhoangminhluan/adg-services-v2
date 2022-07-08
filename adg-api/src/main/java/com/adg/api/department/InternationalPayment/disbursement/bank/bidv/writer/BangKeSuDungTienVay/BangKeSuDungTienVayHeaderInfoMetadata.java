package com.adg.api.department.InternationalPayment.disbursement.bank.bidv.writer.BangKeSuDungTienVay;

import com.adg.api.department.InternationalPayment.disbursement.NhaCungCapDTO;
import com.adg.api.department.InternationalPayment.disbursement.office.AdgExcelTableHeaderInfo;
import com.adg.api.department.InternationalPayment.disbursement.reader.header.HoaDonHeaderMetadata;
import com.merlin.asset.core.utils.MapUtils;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.30 13:43
 */
public enum BangKeSuDungTienVayHeaderInfoMetadata implements AdgExcelTableHeaderInfo {
    TT(
            "TT",
            "A9",
            CellType.NUMERIC,
            record -> MapUtils.getString(record, HoaDonHeaderMetadata.SoThuTuKhongGop.deAccentedName)
    ),
    NoiDung(
            "Nội dung",
            "B9",
            CellType.STRING,
            record -> String.format("Thanh toán tiền hàng theo hoá đơn %s hết", "0" + MapUtils.getString(record, HoaDonHeaderMetadata.SoHoaDon.deAccentedName).replace("0", ""))
    ),
    SoHieuChungTuKeToan(
            "Số hiệu chứng từ kế toán",
            "C9",
            CellType.STRING,
            record -> "UNC"
    ),
    SoTien(
            "Số tiền (VNĐ)",
            "D9",
            CellType.NUMERIC,
            record -> MapUtils.getString(record, HoaDonHeaderMetadata.TongTienThanhToan.deAccentedName)
    ),
    SoHopDong(
            "Số hợp đồng",
            "E9",
            CellType.STRING,
            record -> ""
    ),
    TenDonViVaSoTK(
            "Tên đơn vị, số tài khoản ngân hàng người thụ hưởng",
            "F9",
            CellType.STRING,
            record -> {
                String nhaCungCap = MapUtils.getString(record, HoaDonHeaderMetadata.NhaCungCap.deAccentedName);
                NhaCungCapDTO dto = NhaCungCapDTO.nhaCungCapMap.get(nhaCungCap);
                if (dto == null) {
                    return String.format("\nSỐ TK: \nTẠI NH: ");
                }
                return String.format("%s\nSỐ TK: %s\nTẠI NH: %s", dto.getTenKhachHang(), dto.getSoTaiKhoan(), dto.getTenNganHang());
            }
    );

    private final String header;
    private final String cellAddress;
    private final CellType cellType;
    public final Function<Map<String, Object>, String> transformCallback;

    BangKeSuDungTienVayHeaderInfoMetadata(String header, String cellAddress, CellType cellType, Function<Map<String, Object>, String> transformCallback) {
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
