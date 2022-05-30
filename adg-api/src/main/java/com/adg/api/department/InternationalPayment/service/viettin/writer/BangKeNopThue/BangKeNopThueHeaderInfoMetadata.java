package com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeNopThue;

import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderInfo;
import com.adg.api.department.InternationalPayment.service.viettin.reader.ToKhaiHaiQuanHeaderInfoMetadata;
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
            "D25",
            CellType.NUMERIC,
            record -> ""
    ),
    ID(
            "Số tờ TK/số QĐ/số TB/ Mã định danh hồ sơ (ID)",
            "E25",
            CellType.STRING,
            record -> MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.SoToKhai.deAccentedName)
    ),
    KyThue(
            "Kỳ thuế/ngày QĐ/ngày TB",
            "I25",
            CellType.STRING,
            record -> MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.NgayDangKy.deAccentedName)
    ),
    NoiDungKhoanNop(
         "Nội dung khoản nộp NSNN",
         "J25",
            CellType.STRING,
            record -> MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.NoiDungKhoanNop.deAccentedName)
    ),
    MaNDKTTM(
            "Mã NDKTTM",
            "K25",
            CellType.NUMERIC,
            record -> MapUtils.getString(record, ToKhaiHaiQuanHeaderInfoMetadata.MaNDKTTM.deAccentedName)
    ),
    SoTien(
            "Số tiền",
            "L25",
            CellType.NUMERIC,
            record -> ""
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
        return null;
    }

    @Override
    public String getCellAddress() {
        return null;
    }

    @Override
    public int getOrdinal() {
        return 0;
    }

    @Override
    public CellType getCellType() {
        return null;
    }

    @Override
    public boolean isGroupedColumn() {
        return false;
    }
}
