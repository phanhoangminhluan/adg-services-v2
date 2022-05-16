package com.adg.api.department.InternationalPayment.general;

import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.02 10:13
 */
public interface AdgWordTableHeaderInfo {

    String getHeaderName();
    int getOrdinal();
    Consumer<XWPFTableCell> cellFormatConsumer();
    Consumer<List<XWPFRun>> runConsumer();

}
