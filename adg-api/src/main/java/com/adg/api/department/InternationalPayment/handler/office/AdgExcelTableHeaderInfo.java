package com.adg.api.department.InternationalPayment.handler.office;

import org.apache.poi.ss.usermodel.CellType;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.24 11:21
 */
public interface AdgExcelTableHeaderInfo {

    String getHeaderName();
    String getCellAddress();
    int getOrdinal();
    CellType getCellType();
    boolean isGroupedColumn();
}
