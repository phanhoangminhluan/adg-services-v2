package com.adg.api.department.InternationalPayment.bank.viettin.writer.BangKeChungTuDeNghiGiaiNgan;

import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderInfo;
import org.apache.poi.ss.usermodel.CellType;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.24 11:22
 */
public enum BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo implements AdgExcelTableHeaderInfo {

    TT("TT", "A10", CellType.NUMERIC, false),
    SoChungTu("Số chứng từ", "B10", CellType.NUMERIC, false),
    NgayChungTu("Ngày chứng từ", "C10", CellType.STRING, false),
    SoTien("Số tiền (VND)", "D10", CellType.NUMERIC, false),
    DonViPhatHanh("Đơn vị phát hành", "E10", CellType.STRING, true),
    GhiChu("Ghi chú", "F10", CellType.STRING, false);

    private final String header;
    private final String cellAddress;
    private final CellType cellType;
    private final boolean isGroupedColumn;

    BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo(String header, String cellAddress, CellType cellType, boolean isGroupedColumn) {
        this.header = header;
        this.cellAddress = cellAddress;
        this.cellType = cellType;
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
