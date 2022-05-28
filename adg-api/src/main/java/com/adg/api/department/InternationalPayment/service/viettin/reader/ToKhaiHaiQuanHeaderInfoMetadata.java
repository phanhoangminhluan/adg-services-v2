package com.adg.api.department.InternationalPayment.service.viettin.reader;

import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderInfo;
import com.adg.api.department.InternationalPayment.service.bidv.NhaCungCapDTO;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.StringUtils;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.28 22:20
 */
public enum ToKhaiHaiQuanHeaderInfoMetadata implements AdgExcelTableHeaderInfo {
    SoToKhai(
            "Số tờ khai",
            "",
            CellType.STRING,
            record -> MapUtils.getString(record, "Số tờ khai")

    ),
    TenCoQuan(
            "Tên cơ quan Hải quan tiếp nhận tờ khai",
            "",
            CellType.STRING,
            record -> {
                NhaCungCapDTO nhaCungCapDTO = NhaCungCapDTO.maNhaCungCapMap.get(MapUtils.getString(record, "Tên cơ quan Hải quan tiếp nhận tờ khai"));
                return nhaCungCapDTO == null ? "xxx-xxx-xxx" : nhaCungCapDTO.getTenKhachHang();
            }
    ),
    TongTienThue(
            "Tổng tiền thuế phải nộp",
            "",
            CellType.NUMERIC,
            record -> MapUtils.getListString(record, "Tổng tiền thuế phải nộp").get(0).replace(".", "")
    ),
    NgayDangKy(
            "Ngày đăng ký",
            "",
            CellType.STRING,
            record -> MapUtils.getString(record, "Ngày đăng ký").split(" ")[0]
    )

    ;

    private final String header;
    public final String deAccentedHeader;
    private final String cellAddress;
    private final CellType cellType;
    public final Function<Map<String, Object>, String> transformCallback;

    ToKhaiHaiQuanHeaderInfoMetadata(String header, String cellAddress, CellType cellType, Function<Map<String, Object>, String> transformCallback) {
        this.header = header;
        this.deAccentedHeader = StringUtils.makeCamelCase(header);
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
}
