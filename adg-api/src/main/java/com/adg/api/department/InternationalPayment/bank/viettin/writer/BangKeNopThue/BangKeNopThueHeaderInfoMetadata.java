package com.adg.api.department.InternationalPayment.bank.viettin.writer.BangKeNopThue;

import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderInfo;
import com.adg.api.department.InternationalPayment.reader.header.ToKhaiHaiQuanHeaderInfoMetadata;
import com.merlin.asset.core.utils.MapUtils;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.29 19:13
 */
public enum BangKeNopThueHeaderInfoMetadata implements AdgExcelTableHeaderInfo {
    STT(
            "STT",
//            "D25",
            "B25",
            CellType.NUMERIC,
            record -> ""
    ),
    ID(
            "Số tờ TK/số QĐ/số TB/ Mã định danh hồ sơ (ID)",
//            "E25",
            "C25",
            CellType.STRING,
            record -> {
                String soToKhai = MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.deAccentedName);
                return soToKhai.substring(0, soToKhai.length() - 1);
            }
    ),
    KyThue(
            "Kỳ thuế/ngày QĐ/ngày TB",
//            "I25",
            "D25",
            CellType.STRING,
            record -> MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.NgayDangKy.deAccentedName)
    ),
    NoiDungKhoanNop(
         "Nội dung khoản nộp NSNN",
//         "J25",
         "E25",
            CellType.STRING,
            record -> "Thuế GTGT hàng NK"
    ),
    MaNDKTTM(
            "Mã NDKTTM",
//            "K25",
            "F25",
            CellType.NUMERIC,
            record -> "1702"
    ),
    SoTien(
            "Số tiền",
//            "L25",
            "G25",
            CellType.NUMERIC,
            record -> MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.deAccentedName)
    )

    ;
    private final String header;
    private final String cellAddress;
    private final CellType cellType;
    public final Function<Map<String, Object>, String> transformToKhaiHaiQuanCallback;

    BangKeNopThueHeaderInfoMetadata(String header, String cellAddress, CellType cellType, Function<Map<String, Object>, String> transformToKhaiHaiQuanCallback) {
        this.header = header;
        this.cellAddress = cellAddress;
        this.cellType = cellType;
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

    @Override
    public boolean isGroupedColumn() {
        return false;
    }
}
