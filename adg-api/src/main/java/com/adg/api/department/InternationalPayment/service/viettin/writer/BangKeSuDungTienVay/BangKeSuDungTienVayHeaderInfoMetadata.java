package com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeSuDungTienVay;

import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderInfo;
import com.adg.api.department.InternationalPayment.service.bidv.NhaCungCapDTO;
import com.adg.api.department.InternationalPayment.service.bidv.enums.HoaDonHeaderMetadata;
import com.adg.api.department.InternationalPayment.service.viettin.reader.ToKhaiHaiQuanHeaderInfoMetadata;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.29 08:10
 */
public enum BangKeSuDungTienVayHeaderInfoMetadata implements AdgExcelTableHeaderInfo {

    TT(
                    "TT",
            "A6",
            CellType.NUMERIC,
            record -> MapUtils.getString(record, HoaDonHeaderMetadata.SoThuTuKhongGop.deAccentedName),
            record -> MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.SoThuTuKhongGop.deAccentedName)
    ),
    TenKhachHang(
            "TÊN KHÁCH HÀNG",
            "B6",
            CellType.STRING,
            record -> MapUtils.getString(record, HoaDonHeaderMetadata.NhaCungCap.deAccentedName),
            record -> MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.deAccentedName)
    ),
    TaiKhoan(
            "TÀI KHOẢN",
            "C6",
            CellType.STRING,
            record -> {
                String nhaCungCap = MapUtils.getString(record, HoaDonHeaderMetadata.NhaCungCap.deAccentedName);
                NhaCungCapDTO dto = NhaCungCapDTO.nhaCungCapMap.get(nhaCungCap);
                return dto == null ? "xxxx-xxxx-xxxx" : dto.getSoTaiKhoan();
            },
            record -> {
                String nhaCungCap = MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.deAccentedName);
                NhaCungCapDTO dto = NhaCungCapDTO.nhaCungCapMap.get(nhaCungCap);
                return dto == null ? "xxxx-xxxx-xxxx" : dto.getSoTaiKhoan();
            }
    ),
    NganHang(
            "NGÂN HÀNG",
            "D6",
            CellType.STRING,
            record -> {
                String nhaCungCap = MapUtils.getString(record, HoaDonHeaderMetadata.NhaCungCap.deAccentedName);
                NhaCungCapDTO dto = NhaCungCapDTO.nhaCungCapMap.get(nhaCungCap);
                return dto == null ? "xxxx-xxxx-xxxx" : dto.getTenNganHang();
            },
            record -> {
                String nhaCungCap = MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.deAccentedName);
                NhaCungCapDTO dto = NhaCungCapDTO.nhaCungCapMap.get(nhaCungCap);
                return dto == null ? "xxxx-xxxx-xxxx" : dto.getTenNganHang();
            }
    ),
    MucDich(
            "MỤC ĐÍCH",
            "E6",
            CellType.STRING,
            record -> "Thanh toán tiền hạt nhựa",
            record -> "Thanh toán tiền thuế GTGT, thuế NK hàng NK"
    ),
    SoChungTu(
            "SỐ CHỨNG TỪ",
            "F6",
            CellType.STRING,
            record -> "0" + MapUtils.getString(record, HoaDonHeaderMetadata.SoHoaDon.deAccentedName).replace("0", ""),
            record -> MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.deAccentedName)
            ),
    NgayChungTu(
            "NGÀY CHỨNG TỪ",
            "G6",
            CellType.STRING,
            record -> DateTimeUtils
                    .reformatDate(
                            MapUtils.getString(record, HoaDonHeaderMetadata.NgayChungTu.deAccentedName),
                            DateTimeUtils.FMT_01,
                            DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_09),
                            "UTC",
                            "UTC"
                    ),
            record -> MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.NgayDangKy.deAccentedName)
    ),
    SoTien(
            "SỐ TIỀN (VND)",
            "H6",
            CellType.NUMERIC,
            record -> MapUtils.getString(record, HoaDonHeaderMetadata.TongTienThanhToan.deAccentedName),
            record -> MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.deAccentedName)
    ),
    SoTienDaThanhToan(
            "SỐ TIỀN ĐÃ THANH TOÁN (VND)",
            "I6",
            CellType.STRING,
            record -> "-",
            record -> "-"
    ),
    SoTienNhanNo(
            "SỐ TIỀN NHẬN NỢ (VND)",
            "J6",
            CellType.NUMERIC,
            record -> MapUtils.getString(record, HoaDonHeaderMetadata.TongTienThanhToan.deAccentedName),
            record -> MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.deAccentedName)
    ),
    PhuongThucGiaiNgan(
            "PHƯƠNG THỨC GIẢI NGÂN",
            "K6",
            CellType.STRING,
            record -> "CK",
            record -> "CK"
    )
    ;
    private final String header;
    private final String cellAddress;
    private final CellType cellType;
    public final Function<Map<String, Object>, String> transformHoaDonCallback;
    public final Function<Map<String, Object>, String> transformToKhaiHaiQuanCallback;

    BangKeSuDungTienVayHeaderInfoMetadata(String header, String cellAddress, CellType cellType, Function<Map<String, Object>, String> transformHoaDonCallback, Function<Map<String, Object>, String> transformToKhaiHaiQuanCallback) {
        this.header = header;
        this.cellAddress = cellAddress;
        this.cellType = cellType;
        this.transformHoaDonCallback = transformHoaDonCallback;
        this.transformToKhaiHaiQuanCallback = transformToKhaiHaiQuanCallback;
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
}
