package com.adg.api.department.InternationalPayment.service.viettin;

import com.adg.api.department.InternationalPayment.facade.AdgExcelTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelTable;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelUtils;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelWriter;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.24 15:18
 */
//@Service
public class KeDienTuService {

    private ExcelWriter excelWriter;
    private ExcelTable excelTable;

//    @Value("${adg.accounting.ke_dien_tu}")
    private String filePath = "/Users/luan.phm/engineering/Projects/ADongGroup/adg-services/adg-api/src/main/resources/2. Kê Điện Tử.xlsx";

    public KeDienTuService() {
        this.excelWriter = new ExcelWriter(filePath);
        this.excelWriter.openSheet();
        this.excelTable = new ExcelTable(this.excelWriter, AdgExcelTableHeaderMetadata.getBangKeChungTuDeNghiGiaiNgan());
    }

    public void insertRecordToTable(List<Map<String, Object>> items) {
        items.forEach(item -> this.excelTable.insert(item));
    }

    public void fillSignDate() {
        String originalCellAddress = "D17";
        Cell originalCell = this.excelWriter.getCell(originalCellAddress);
        Cell shiftedCell = this.excelWriter
                .getCell(
                        this.excelWriter.getRow(this.excelTable.getSize() + originalCell.getRowIndex() - 1),
                        originalCell.getColumnIndex()
                );
        String val = String.format("TP HCM, ngày %s tháng %s năm %s", ZonedDateTime.now().getDayOfMonth(), ZonedDateTime.now().getMonth().getValue(), ZonedDateTime.now().getYear());
        shiftedCell.setCellValue(val);
    }

    public void fillSum() {
        Cell soTienHeaderCell = this.excelWriter.getCell(BangKeChungTuDienTuDeNghiGiaiNganTableMetadataHeaderInfo.SoTien.getCellAddress());
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
    }

    public void build() {
        String folder = "/Users/luan.phm/engineering/Projects/ADongGroup/adg-services/adg-api/src/main/resources/output";
        String fileName = String.format("Kê Điện Tử - %s.xlsx", DateTimeUtils.convertZonedDateTimeToFormat(ZonedDateTime.now(), "Asia/Ho_Chi_Minh", DateTimeUtils.MA_DATE_TIME_FORMATTER));
        this.excelWriter.build(folder + "/" + fileName);
    }


    public static void main(String[] args) {
        Map<String, Object> item = MapUtils.ImmutableMap()
                .put("TT", "1")
                .put("Số chứng từ", "12345")
                .put("Ngày chứng từ", "23/12/1998")
                .put("Số tiền (VND)", "100000000")
                .put("Đơn vị phát hành", "Chi cục HQ CK Cảng Sài Gòn KV I")
                .put("Ghi chú", "TKHQ")
                .build();

        Map<String, Object> item1 = MapUtils.ImmutableMap()
                .put("TT", "2")
                .put("Số chứng từ", "6789")
                .put("Ngày chứng từ", "23/12/1998")
                .put("Số tiền (VND)", "100000000")
                .put("Đơn vị phát hành", "Chi cục HQ CK Cảng Sài Gòn KV I")
                .put("Ghi chú", "TKHQ")
                .build();

        Map<String, Object> item2 = MapUtils.ImmutableMap()
                .put("TT", "3")
                .put("Số chứng từ", "1011112")
                .put("Ngày chứng từ", "23/12/1998")
                .put("Số tiền (VND)", "100000000")
                .put("Đơn vị phát hành", "Chi cục HQ CK Cảng Sài Gòn KV I")
                .put("Ghi chú", "TKHQ")
                .build();

        KeDienTuService keDienTuService = new KeDienTuService();
        keDienTuService.insertRecordToTable(Arrays.asList(item, item1, item2));
        keDienTuService.excelTable.removeSampleRow();
        keDienTuService.fillSum();
        keDienTuService.fillSignDate();
        keDienTuService.build();

    }

}
