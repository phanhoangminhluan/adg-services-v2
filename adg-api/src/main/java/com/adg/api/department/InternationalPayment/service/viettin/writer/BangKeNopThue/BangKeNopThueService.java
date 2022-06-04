package com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeNopThue;

import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelTable;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelUtils;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelWriter;
import com.adg.api.department.InternationalPayment.service.bidv.NhaCungCapDTO;
import com.adg.api.department.InternationalPayment.service.viettin.reader.ToKhaiHaiQuanHeaderInfoMetadata;
import com.adg.api.util.MoneyUtils;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.core.io.Resource;

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

    private final ExcelWriter excelWriter;
    private final ExcelTable excelTable;
    private final Map<String, Object> data;
    private final String outputFolder;
    private final ZonedDateTime fileDate;
    private double totalCost = 0;
    private final String nhaCungCap;
    private final String soToKhai;


    public BangKeNopThueService(String outputFolder, List<Map<String, Object>> toKhaiHaiQuanRecords, String soToKhai, ZonedDateTime fileDate, InputStream inputStream) {
        this.outputFolder = outputFolder;
        this.excelWriter = new ExcelWriter(inputStream);
        this.excelWriter.openSheet();
        this.excelTable = new ExcelTable(this.excelWriter, AdgExcelTableHeaderMetadata.getBangKeNopThue());
        this.fileDate = fileDate;
        this.nhaCungCap = MapUtils.getString(toKhaiHaiQuanRecords.get(0), ToKhaiHaiQuanHeaderInfoMetadata.TenCoQuan.deAccentedName);
        this.soToKhai = soToKhai;
        this.data = this.transformRecords(toKhaiHaiQuanRecords);
    }

    @SneakyThrows
    public static void writeOut(String outputFolder, Map<String, Object> toKhaiHaiQuanRecordsGroupBySoToKhai, ZonedDateTime fileDate, Resource resource) {
        for (String soToKhai : toKhaiHaiQuanRecordsGroupBySoToKhai.keySet()) {
            new BangKeNopThueService(outputFolder, MapUtils.getListMapStringObject(toKhaiHaiQuanRecordsGroupBySoToKhai, soToKhai), soToKhai, fileDate, resource.getInputStream())
                    .exportDocument();
        }
    }

    private Map<String, Object> transformRecords(List<Map<String, Object>> toKhaiHaiQuanRecords) {
        List<Map<String, Object>> table = new ArrayList<>();
        int stt = 1;
        for (Map<String, Object> toKhaiHaiQuanRecord : toKhaiHaiQuanRecords) {
            Map<String, Object> record = new HashMap<>();
            for (BangKeNopThueHeaderInfoMetadata headerInfo : BangKeNopThueHeaderInfoMetadata.values()) {
                record.put(headerInfo.getHeaderName(), headerInfo.transformToKhaiHaiQuanCallback.apply(toKhaiHaiQuanRecord));
            }

            totalCost += MapUtils.getDouble(toKhaiHaiQuanRecord, ToKhaiHaiQuanHeaderInfoMetadata.TongTienThue.deAccentedName);

            List<Map<String, Object>> chiTietThue = MapUtils.getListMapStringObject(toKhaiHaiQuanRecord, ToKhaiHaiQuanHeaderInfoMetadata.ChiTietThue.deAccentedName);
            for (Map<String, Object> map : chiTietThue) {
                Map<String, Object> clonedRecord = new HashMap<>(record);
                String tenSacThue =  MapUtils.getString(map, ToKhaiHaiQuanHeaderInfoMetadata.TenSacThue.deAccentedName);

                clonedRecord.put(BangKeNopThueHeaderInfoMetadata.STT.getHeaderName(), stt);
                clonedRecord.put(BangKeNopThueHeaderInfoMetadata.SoTien.getHeaderName(), MapUtils.getString(map, ToKhaiHaiQuanHeaderInfoMetadata.TienThue.deAccentedName));
                if (tenSacThue.equals("Thuế NK")) {
                    clonedRecord.put(BangKeNopThueHeaderInfoMetadata.MaNDKTTM.getHeaderName(), "1901");
                    clonedRecord.put(BangKeNopThueHeaderInfoMetadata.NoiDungKhoanNop.getHeaderName(), "Thuế nhập khẩu");
                }

                table.add(clonedRecord);

                stt++;
            }
        }
        return MapUtils.ImmutableMap()
                .put("Bảng kê", table)
                .build();
    }
    public void exportDocument() {
        this.insertRecordToTable();
        this.fillData();
        this.build();
    }

    private void insertRecordToTable() {
        List<Map<String, Object>> records = MapUtils.getListMapStringObject(this.data, "Bảng kê");
        records.forEach(item -> this.excelTable.insert2(item));
        this.excelTable.removeSampleRow();
    }

    private void build() {
        String fileName = String.format("Bảng kê nộp thuế - %s - %s.xlsx",
                this.soToKhai,
                DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_03)
        );
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

        ExcelUtils.setCell(this.excelWriter.getCell("H19"), nganHang, CellType.STRING);
        ExcelUtils.setCell(this.excelWriter.getCell("H24"), this.nhaCungCap, CellType.STRING);
    }

}
