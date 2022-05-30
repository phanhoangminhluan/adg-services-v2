package com.adg.api.department.InternationalPayment.service.bidv.writer.BienBanKiemTraSuDungVonVay;

import com.adg.api.department.InternationalPayment.handler.office.AdgWordTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.handler.office.word.WordUtils;
import com.adg.api.department.InternationalPayment.handler.office.word.WordWriter;
import com.adg.api.department.InternationalPayment.service.bidv.enums.HoaDonHeaderMetadata;
import com.adg.api.util.MoneyUtils;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.NumberUtils;
import com.merlin.asset.core.utils.ParserUtils;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.09 22:25
 */
public class BienBanKiemTraSuDungVonVayService {

    private final WordWriter wordWriter;
    private final String outputFolder;
    private final Map<String, Object> data;
    private final ZonedDateTime fileDate;

    public BienBanKiemTraSuDungVonVayService(String outputFolder, Map<String, Object> hoaDonRecords, ZonedDateTime fileDate, InputStream inputStream) {
        this.wordWriter = new WordWriter(inputStream, AdgWordTableHeaderMetadata.getHeaderBienBanKiemTraSuDungVonVay());
        this.outputFolder = outputFolder;
        this.fileDate = fileDate;
        this.data = this.transformHoaDonRecords(hoaDonRecords);
    }

    public void exportDocument() {
        this.fillTextData();
        this.fillTableData();
        this.fillTableSumData();
        this.build();
    }

    public Map<String, Object> transformHoaDonRecords(Map<String, Object> hoaDonRecords) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> arr = new ArrayList<>();
        double tongTienVay = 0;
        for (String nhaCungCap : hoaDonRecords.keySet()) {
            Map<String, Object> hoaDonByNcc = MapUtils.getMapStringObject(hoaDonRecords, nhaCungCap);
            Map<String, Object> transformedRecord = new HashMap<>();
            for (BienBanKiemTraSuDungVonVayHeaderInfoMetadata headerInfoMetadata : BienBanKiemTraSuDungVonVayHeaderInfoMetadata.values()) {
                transformedRecord.put(headerInfoMetadata.getHeaderName(), headerInfoMetadata.transformCallback.apply(hoaDonByNcc));
                switch (headerInfoMetadata) {
                    case SoTienVND: {
                        tongTienVay += ParserUtils.toDouble(MapUtils.getString(hoaDonByNcc, HoaDonHeaderMetadata.TongTienThanhToanCacHoaDon.deAccentedName));
                    }
                }
            }
            arr.add(transformedRecord);
        }


        result.put("Ngày hôm nay", String.format("ngày %s tháng %s năm %s", this.fileDate.getDayOfMonth(), this.fileDate.getMonthValue(), this.fileDate.getYear()));
        result.put("ngayGiaiNgan", DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_09));
        result.put("Đại diện khách hàng", "");
        result.put("Đại diện ngân hàng", "");
        result.put("Tổng tiền vay bằng số", NumberUtils.formatNumber1(tongTienVay));
        result.put("Số tiền vay", NumberUtils.formatNumber1(tongTienVay));
        result.put("Tổng tiền vay bằng chữ", MoneyUtils.convertMoneyToText(tongTienVay));
        result.put("Danh sách thanh toán tiền hàng", arr);


        return result;
    }

    private void fillTextData() {
        this.wordWriter.fillTextData(data);
    }
    private void fillTableData() {
        this.wordWriter.fillTableData(MapUtils.getListMapStringObject(data, "Danh sách thanh toán tiền hàng"));

    }

    private void fillTableSumData() {
        XWPFTableRow row = this.wordWriter.getWordTable().getTable().createRow();
        XWPFTableCell sumCell = WordUtils.Table.mergeCell(row, 0, 3);
        sumCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        XWPFRun run = WordUtils.Table.setCell(sumCell, "Tổng");
        run.setFontFamily("Times New Roman");
        run.setBold(true);
        run.setFontSize(11);
        WordUtils.Table.makeCenter(sumCell);

        XWPFTableCell calculatedSell = WordUtils.Table.mergeCell(row, 1, 2);
        calculatedSell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        String val = String.format("%s VND (Bằng chữ: %s.)", MapUtils.getString(data, "Tổng tiền vay bằng số"), MapUtils.getString(data, "Tổng tiền vay bằng chữ"));
        XWPFRun calculatedCellRun = WordUtils.Table.setCell(calculatedSell, val);
        calculatedCellRun.setFontSize(11);
        calculatedCellRun.setFontFamily("Times New Roman");
        WordUtils.Table.makeCenter(calculatedSell);

    }

    private void build() {
        String fileName = String.format("Biên bản kiểm tra sử dụng vốn vay - %s.docx",
                DateTimeUtils.convertZonedDateTimeToFormat(
                        this.fileDate,
                        "UTC",
                        DateTimeUtils.FMT_03
                )
        );
        this.wordWriter.build(outputFolder + "/" + fileName);
    }
}
