package com.adg.api.department.InternationalPayment.disbursement.office.word;

import com.adg.api.department.InternationalPayment.disbursement.office.AdgWordTableHeaderInfo;
import com.merlin.asset.core.utils.MapUtils;
import lombok.Getter;
import org.apache.poi.xwpf.usermodel.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.01 16:55
 */
@Getter
public class WordTable {

    protected XWPFDocument document;
    protected XWPFTable table;
    protected Map<Integer, AdgWordTableHeaderInfo> headerMap;

    public WordTable(XWPFDocument document, Map<Integer, AdgWordTableHeaderInfo> headerMap) {
        this.document = document;
        for (IBodyElement bodyElement : this.document.getBodyElements()) {
            if (bodyElement instanceof XWPFTable) {
                XWPFTable xwpfTable = (XWPFTable) bodyElement;
                String rawHeaders = xwpfTable.getText();
                boolean isAllMatch = headerMap.values().stream().allMatch(adgWordTableHeaderInfo -> rawHeaders.contains(adgWordTableHeaderInfo.getHeaderName()));
                if (isAllMatch) {
                    this.table = xwpfTable;
                    break;
                }

            }
        }
        this.headerMap = headerMap;
    }

    public void insertRecord(Map<String, Object> data) {

        XWPFTableRow row = this.table.createRow();

        for (int i = 0; i < headerMap.size(); i++) {
            XWPFTableCell cell = row.getCell(i);
            AdgWordTableHeaderInfo headerInfo = headerMap.get(i);
            List<String> values = MapUtils.getListString(data, headerInfo.getHeaderName(), null);
            if (values == null) {
                String strVal = MapUtils.getString(data, headerInfo.getHeaderName());
                if (strVal.contains("\n")) {
                    values = Arrays.asList(strVal.split("\n"));
                } else {
                    values = Collections.singletonList(MapUtils.getString(data, headerInfo.getHeaderName()));
                }
            }

            List<XWPFRun> runs = WordUtils.Table.setCell(cell, values);

            headerInfo.cellFormatConsumer().accept(cell);
            headerInfo.runConsumer().accept(runs);

        }
    }
}
