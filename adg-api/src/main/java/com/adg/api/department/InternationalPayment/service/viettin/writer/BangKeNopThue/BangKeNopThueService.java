package com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeNopThue;

import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelTable;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelUtils;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelWriter;
import com.adg.api.department.InternationalPayment.service.bidv.NhaCungCapDTO;
import com.adg.api.department.InternationalPayment.service.viettin.reader.ToKhaiHaiQuanHeaderInfoMetadata;
import com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeSuDungTienVay.BangKeSuDungTienVayHeaderInfoMetadata;
import com.adg.api.util.MoneyUtils;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.29 19:13
 */
public class BangKeNopThueService {

    private ExcelWriter excelWriter;
    private ExcelTable excelTable;
    private Map<String, Object> data;
    private String outputFolder;
    private ZonedDateTime fileDate;
    private double totalCost = 0;
    private final String nhaCungCap;


    public BangKeNopThueService(String outputFolder, List<Map<String, Object>> toKhaiHaiQuanRecords, String nhaCungCap, ZonedDateTime fileDate, InputStream inputStream) {
        this.outputFolder = outputFolder;
        this.excelWriter = new ExcelWriter(inputStream);
        this.excelWriter.openSheet();
        this.excelTable = new ExcelTable(this.excelWriter, AdgExcelTableHeaderMetadata.getBangKeNopThue());
        this.fileDate = fileDate;
        this.nhaCungCap = nhaCungCap;
        this.data = this.transformRecords(toKhaiHaiQuanRecords);
    }

    private Map<String, Object> transformRecords(List<Map<String, Object>> toKhaiHaiQuanRecords) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> bangKe = new ArrayList<>();

        int stt = 1;

        for (Map<String, Object> toKhaiHaiQuanRecord : toKhaiHaiQuanRecords) {
            Map<String, Object> transformedRecord = new HashMap<>();
            for (BangKeSuDungTienVayHeaderInfoMetadata headerInfoMetadata : BangKeSuDungTienVayHeaderInfoMetadata.values()) {
                if (headerInfoMetadata == BangKeSuDungTienVayHeaderInfoMetadata.TT) {
                    transformedRecord.put(headerInfoMetadata.getHeaderName(), stt);
                } else {
                    transformedRecord.put(headerInfoMetadata.getHeaderName(), headerInfoMetadata.transformToKhaiHaiQuanCallback.apply(toKhaiHaiQuanRecord));
                }
            }
            stt++;
            totalCost += MapUtils.getDouble(toKhaiHaiQuanRecord, ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.deAccentedName);

            bangKe.add(transformedRecord);
        }
        result.put("Bảng kê", bangKe);
        return result;
    }
    public void exportDocument() {
        this.insertRecordToTable();
        this.fillData();
        this.build();
    }

    private void insertRecordToTable() {
        List<Map<String, Object>> records = MapUtils.getListMapStringObject(this.data, "Bảng kê");
        records.forEach(item -> this.excelTable.insert(item));
        this.excelTable.removeSampleRow();
    }

    private void build() {
        String fileName = String.format("Bảng kê nộp thuế - %s.xlsx", DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_03));
        this.excelWriter.build(outputFolder + "/" + fileName);
    }

    private void fillData() {
        Cell soTienHeaderCell = this.excelWriter.getCell(BangKeNopThueHeaderInfoMetadata.SoTien.getCellAddress());
        String startCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soTienHeaderCell.getRowIndex() + 1),
                soTienHeaderCell.getColumnIndex()
        ).getAddress().formatAsString();

        String endCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soTienHeaderCell.getRowIndex() + this.excelTable.getSize()),
                soTienHeaderCell.getColumnIndex()
        ).getAddress().formatAsString();

        Cell tongCell = this.excelWriter.getCell(
                this.excelWriter.getRow(soTienHeaderCell.getRowIndex() + this.excelTable.getSize() + 1),
                soTienHeaderCell.getColumnIndex()
        );

        ExcelUtils.setCell(tongCell, String.format("SUM(%s:%s)", startCell, endCell), CellType.FORMULA);

        Cell kyThueHeaderCell =  this.excelWriter.getCell(BangKeNopThueHeaderInfoMetadata.KyThue.getCellAddress());
        Cell soTienBangChuCell = this.excelWriter.getCell(
                this.excelWriter.getRow(kyThueHeaderCell.getRowIndex() + this.excelTable.getSize() + 2),
                soTienHeaderCell.getColumnIndex()
        );
        ExcelUtils.setCell(soTienBangChuCell, MoneyUtils.convertMoneyToText(this.totalCost),CellType.STRING);

        ExcelUtils.setCell(tongCell, String.format("SUM(%s:%s)", startCell, endCell), CellType.FORMULA);

        NhaCungCapDTO nhaCungCapDTO = NhaCungCapDTO.nhaCungCapMap.get(this.nhaCungCap);
        String nganHang = "xxxx-xxxx-xxxx";
        if (nhaCungCapDTO != null) {
            nganHang = nhaCungCapDTO.getTenNganHang();
        }

        ExcelUtils.setCell(this.excelWriter.getCell("H19"), nganHang, CellType.FORMULA);
        ExcelUtils.setCell(this.excelWriter.getCell("H24"), this.nhaCungCap, CellType.FORMULA);
    }

}
