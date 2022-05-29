package com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeChungTuDeNghiGiaiNgan;

import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderInfo;
import org.apache.poi.ss.usermodel.CellType;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.24 11:22
 */
public enum BangKeChungTuDienTuDeNghiGiaiNganTableMetadataHeaderInfo implements AdgExcelTableHeaderInfo {

    TT("TT", "A9", CellType.NUMERIC),
    SoChungTu("Số chứng từ", "B9", CellType.NUMERIC),
    NgayChungTu("Ngày chứng từ", "C9", CellType.STRING),
    SoTien("Số tiền (VND)", "D9", CellType.NUMERIC),
    DonViPhatHanh("Đơn vị phát hành", "E9", CellType.STRING),
    GhiChu("Ghi chú", "F9", CellType.STRING);

    private final String header;
    private final String cellAddress;
    private final CellType cellType;

    BangKeChungTuDienTuDeNghiGiaiNganTableMetadataHeaderInfo(String header, String cellAddress, CellType cellType) {
        this.header = header;
        this.cellAddress = cellAddress;
        this.cellType = cellType;
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
