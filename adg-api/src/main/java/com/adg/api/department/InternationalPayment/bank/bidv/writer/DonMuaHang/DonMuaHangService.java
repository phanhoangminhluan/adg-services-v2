package com.adg.api.department.InternationalPayment.bank.bidv.writer.DonMuaHang;

import com.adg.api.department.InternationalPayment.NhaCungCapDTO;
import com.adg.api.department.InternationalPayment.handler.office.AdgExcelTableHeaderMetadata;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelTable;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelUtils;
import com.adg.api.department.InternationalPayment.handler.office.excel.ExcelWriter;
import com.adg.api.department.InternationalPayment.reader.header.HoaDonHeaderMetadata;
import com.adg.api.department.InternationalPayment.reader.header.PhieuNhapKhoHeaderMetadata;
import com.adg.api.department.InternationalPayment.reader.service.HoaDonService;
import com.adg.api.util.MoneyUtils;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
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
 * Created on: 2022.05.03 12:24
 */
@Log4j2
public class DonMuaHangService {

    private ExcelWriter excelWriter;
    private ExcelTable excelTable;
    private Map<String, Object> data;
    private String outputFolder;
    private String ncc;
    private String soHoaDon;
    private ZonedDateTime fileDate;
    private String ngayChungTu;

    private static class DonMuaHangAddress {
        public static final String TEN_NCC = "B8";
        public static final String NGAY = "F8";
        public static final String DIA_CHI = "B9";
        public static final String SO = "F9";
        public static final String MA_SO_THUE = "B10";
        public static final String LOAI_TIEN = "F10";
        public static final String DIEN_THOAI = "B11";
        public static final String FAX = "F11";
        public static final String DIEN_GIAI = "B12";
    }

    public DonMuaHangService(String outputFolder, List<Map<String, Object>> phieuNhapKhoRecords, String nhaCungCap, String ngayChungTu, ZonedDateTime fileDate, InputStream templateInputStream) {
        this.outputFolder = outputFolder;
        this.excelWriter = new ExcelWriter(templateInputStream);
        this.excelWriter.openSheet();
        this.excelTable = new ExcelTable(
                this.excelWriter,
                AdgExcelTableHeaderMetadata.getDonMuaHang()
        );
        this.fileDate = fileDate;
        this.ngayChungTu = ngayChungTu;
        this.ncc = nhaCungCap;
        this.data = this.transformPhieuNhapKhoRecords(phieuNhapKhoRecords);
    }

    @SneakyThrows
    public static Map<String, Object> writeOut(
            String outputFolder,
            Map<String, Object> hoaDonRecordsGroupByNhaCungCap,
            Map<String, Object> phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon,
            ZonedDateTime fileDate,
            Resource resource
    ) {

        List<Map<String, Object>> statsList = new ArrayList<>();
        long t1 = System.currentTimeMillis();
        for (String nhaCungCap : phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon.keySet()) {
            Map<String, Object> phieuNhapKhoRecordsGroupBySoHoaDon = MapUtils.getMapStringObject(phieuNhapKhoRecordsGroupByNhaCungCapAndSoHoaDon, nhaCungCap);
            for (String soHoaDon : phieuNhapKhoRecordsGroupBySoHoaDon.keySet()) {
                String ngayHoaDon = MapUtils.getString(hoaDonRecordsGroupByNhaCungCap, String.format("%s.%s.%s", nhaCungCap, soHoaDon, HoaDonHeaderMetadata.NgayChungTu.deAccentedName));
                List<Map<String, Object>> phieuNhapKhoRecords = MapUtils.getListMapStringObject(phieuNhapKhoRecordsGroupBySoHoaDon, soHoaDon);
                Map<String, Object> stats = new DonMuaHangService(outputFolder, phieuNhapKhoRecords, nhaCungCap, ngayHoaDon, fileDate, resource.getInputStream()).exportDocument();
                statsList.add(stats);
            }
        }
        return MapUtils.ImmutableMap()
                .put("step", "Generate 'Đơn Mua Hàng'")
                .put("duration", DateTimeUtils.getRunningTimeInSecond(t1))
                .put("detail", statsList)
                .build();

    }

    private Map<String, Object> exportDocument() {
        Map<String, Object> stats = new HashMap<>();
        try {
            long t1 = System.currentTimeMillis();
            this.insertRecordToTable();
            stats.put("fillTableDuration", DateTimeUtils.getRunningTimeInSecond(t1));

            t1 = System.currentTimeMillis();
            this.fillUpperData();
            this.fillTongTien();
            this.fillLowerData();
            stats.put("fillOtherDataDuration", DateTimeUtils.getRunningTimeInSecond(t1));

            t1 = System.currentTimeMillis();
            String fileName = this.build();
            stats.put("fileName", fileName);
            stats.put("writeFileDuration", DateTimeUtils.getRunningTimeInSecond(t1));
        } finally {
            log.info("Step: {}. File name: {}. Fill table duration: {}. Fill other data duration: {}. Write file duration: {}",
                    "Generate 'Đơn Mua Hàng'",
                    MapUtils.getString(stats, "fileName"),
                    MapUtils.getString(stats, "fillTableDuration"),
                    MapUtils.getString(stats, "fillOtherDataDuration"),
                    MapUtils.getString(stats, "writeFileDuration")
            );
        }

        return stats;

    }

    private String build() {
        String fileName = String.format("Đơn mua hàng - %s - %s - %s.xlsx",
                this.soHoaDon,
                MapUtils.getString(this.data, "shortNameNcc"),
                DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.FMT_03)
        );
        this.excelWriter.build(this.outputFolder + "/" + fileName);
        return fileName;
    }

    private Map<String, Object> transformPhieuNhapKhoRecords(List<Map<String, Object>> phieuNhapKhoRecords) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> table = new ArrayList<>();
        String tenNcc = "";
        double thanhTien = 0;
        for (Map<String, Object> phieuNhapKhoRecord : phieuNhapKhoRecords) {
            Map<String, Object> transformedRecord = new HashMap<>();
            for (DonMuaHangHeaderInfoMetadata headerInfoMetadata : DonMuaHangHeaderInfoMetadata.values()) {
                transformedRecord.put(headerInfoMetadata.getHeaderName(), headerInfoMetadata.transformCallback.apply(phieuNhapKhoRecord));
                switch (headerInfoMetadata) {
                    case ThanhTien: {
                        thanhTien += Math.round((MapUtils.getDouble(phieuNhapKhoRecord, PhieuNhapKhoHeaderMetadata.ThanhTien.deAccentedName) * 1.1));
                    }
                }
            }
            tenNcc = MapUtils.getString(phieuNhapKhoRecord, PhieuNhapKhoHeaderMetadata.NhaCungCap.deAccentedName).trim();
            table.add(transformedRecord);
            this.soHoaDon = HoaDonService.transformSoHoaDon(MapUtils.getString(phieuNhapKhoRecord, PhieuNhapKhoHeaderMetadata.SoHoaDon.deAccentedName));
        }

        String shortNameNcc = NhaCungCapDTO.nhaCungCapMap.get(ncc) == null ? "xxx-" + System.currentTimeMillis() : NhaCungCapDTO.nhaCungCapMap.get(ncc).getShortName();

        result.put("Tên NCC", tenNcc);
        result.put("shortNameNcc", shortNameNcc);
        result.put("Ngày", DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.getFormatterWithDefaultValue("dd-MM-yyyy")));
        result.put("Địa chỉ", NhaCungCapDTO.nhaCungCapMap.get(tenNcc) != null ? NhaCungCapDTO.nhaCungCapMap.get(tenNcc).getDiaChi() : "xxx-xxx-xxx");
        result.put("Số", String.format("ĐMH%s",DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "UTC", DateTimeUtils.getFormatterWithDefaultValue("ddMMyy"))));
        result.put("Mã số thuế", NhaCungCapDTO.nhaCungCapMap.get(tenNcc) != null ? NhaCungCapDTO.nhaCungCapMap.get(tenNcc).getMaSoThue() : "xxx-xxx-xxx");
        result.put("Loại tiền", "VND");
        result.put("Điện thoại", "");
        result.put("Fax", "");
        result.put("Diễn giải", "");
        result.put("Danh sách mã hàng", table);
        result.put("Số tiền viết bằng chữ", MoneyUtils.convertMoneyToText(thanhTien));
        result.put("Ngày giao hàng", String.format("Kể từ ngày %s", DateTimeUtils.reformatDate(this.ngayChungTu, DateTimeUtils.FMT_01, DateTimeUtils.FMT_09)));
        result.put("Địa điểm giao hàng", "Tại kho Công ty Cổ Phần Á Đông ADG");
        result.put("Điều khoản thanh toán", "");
        result.put("Ghi chú", "");

        return result;
    }


    private void insertRecordToTable() {
        List<Map<String, Object>> records = MapUtils.getListMapStringObject(this.data, "Danh sách mã hàng");
        records.forEach(record -> this.excelTable.insert(record));
        this.excelTable.removeSampleRow();
    }

    private void fillTongTien() {
        Cell thanhTienHeaderCell = this.excelWriter.getCell(DonMuaHangHeaderInfoMetadata.ThanhTien.getCellAddress());
        Cell donViCell = this.excelWriter.getCell(DonMuaHangHeaderInfoMetadata.DonVi.getCellAddress());
        String startCell = this.excelWriter.getCell(
                this.excelWriter.getRow(thanhTienHeaderCell.getRowIndex() + 1),
                thanhTienHeaderCell.getColumnIndex()
        ).getAddress().formatAsString();

        String endCell = this.excelWriter.getCell(
                this.excelWriter.getRow(thanhTienHeaderCell.getRowIndex() + this.excelTable.getSize()),
                thanhTienHeaderCell.getColumnIndex()
        ).getAddress().formatAsString();

        Cell tongCell = this.excelWriter.getCell(
                this.excelWriter.getRow(thanhTienHeaderCell.getRowIndex() + this.excelTable.getSize() + 2),
                thanhTienHeaderCell.getColumnIndex()
        );

        Cell thueSuatCell = this.excelWriter.getCell(
                this.excelWriter.getRow(donViCell.getRowIndex() + this.excelTable.getSize() + 2),
                donViCell.getColumnIndex()
        );
        Cell tienThueGTGTCell = this.excelWriter.getCell(
                this.excelWriter.getRow(thanhTienHeaderCell.getRowIndex() + this.excelTable.getSize() + 3),
                thanhTienHeaderCell.getColumnIndex()
        );

        Cell tongTienThanhToanCell = this.excelWriter.getCell(
                this.excelWriter.getRow(thanhTienHeaderCell.getRowIndex() + this.excelTable.getSize() + 4),
                thanhTienHeaderCell.getColumnIndex()
        );


        ExcelUtils.setCell(tongCell, String.format("SUM(%s:%s)", startCell, endCell), CellType.FORMULA);
        ExcelUtils.setCell(tienThueGTGTCell, String.format("%s * %s", tongCell.getAddress().formatAsString(), thueSuatCell.getAddress().formatAsString()), CellType.FORMULA);
        ExcelUtils.setCell(tongTienThanhToanCell, String.format("%s + %s", tongCell.getAddress().formatAsString(), tienThueGTGTCell.getAddress().formatAsString()), CellType.FORMULA);
    }

    private void fillUpperData() {
        String tenNCC = MapUtils.getString(this.data, "Tên NCC");
        String ngay = MapUtils.getString(this.data, "Ngày");
        String diaChi = MapUtils.getString(this.data, "Địa chỉ");
        String so = MapUtils.getString(this.data, "Số");
        String mst = MapUtils.getString(this.data, "Mã số thuế");
        String loaiTien = MapUtils.getString(this.data, "Loại tiền");
        String dienThoai = MapUtils.getString(this.data, "Điện thoại");
        String fax = MapUtils.getString(this.data, "Fax");
        String dienGiai = MapUtils.getString(this.data, "Diễn giải");

        ExcelUtils.setCell(
                this.excelWriter.getCell(DonMuaHangAddress.TEN_NCC),
                tenNCC,
                CellType.STRING
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(DonMuaHangAddress.NGAY),
                ngay,
                CellType.STRING
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(DonMuaHangAddress.DIA_CHI),
                diaChi,
                CellType.STRING
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(DonMuaHangAddress.SO),
                so,
                CellType.STRING
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(DonMuaHangAddress.MA_SO_THUE),
                mst,
                CellType.STRING
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(DonMuaHangAddress.LOAI_TIEN),
                loaiTien,
                CellType.STRING
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(DonMuaHangAddress.DIEN_THOAI),
                dienThoai,
                CellType.STRING
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(DonMuaHangAddress.FAX),
                fax,
                CellType.STRING
        );

        ExcelUtils.setCell(
                this.excelWriter.getCell(DonMuaHangAddress.DIEN_GIAI),
                dienGiai,
                CellType.STRING
        );
    }

    private void fillLowerData() {
        String soTienVietBangChu = MapUtils.getString(this.data, "Số tiền viết bằng chữ");
        String ngayGiaoHang = MapUtils.getString(this.data, "Ngày giao hàng");
        String diaDiemGiaoHang = MapUtils.getString(this.data, "Địa điểm giao hàng");
        String dieuKhoanThanhToan = MapUtils.getString(this.data, "Điều khoản thanh toán");
        String ghiChu = MapUtils.getString(this.data, "Ghi chú");

        Cell donViHeaderCell = this.excelWriter.getCell(DonMuaHangHeaderInfoMetadata.DonVi.getCellAddress());

        Cell soTienVietBangChuCell = this.excelWriter.getCell(
                this.excelWriter.getRow(donViHeaderCell.getRowIndex() + this.excelTable.getSize() + 5),
                donViHeaderCell.getColumnIndex()
        );

        Cell ngayGiaoHangCell = this.excelWriter.getCell(
                this.excelWriter.getRow(donViHeaderCell.getRowIndex() + this.excelTable.getSize() + 7),
                donViHeaderCell.getColumnIndex()
        );

        Cell diaDiemGiaoHangCell = this.excelWriter.getCell(
                this.excelWriter.getRow(donViHeaderCell.getRowIndex() + this.excelTable.getSize() + 8),
                donViHeaderCell.getColumnIndex()
        );

        Cell dieuKhoanThanhToanCell = this.excelWriter.getCell(
                this.excelWriter.getRow(donViHeaderCell.getRowIndex() + this.excelTable.getSize() + 9),
                donViHeaderCell.getColumnIndex()
        );

        Cell ghiChuCell = this.excelWriter.getCell(
                this.excelWriter.getRow(donViHeaderCell.getRowIndex() + this.excelTable.getSize() + 10),
                donViHeaderCell.getColumnIndex()
        );

        ExcelUtils.setCell(
                soTienVietBangChuCell,
                soTienVietBangChu,
                CellType.STRING
        );

        ExcelUtils.setCell(
                ngayGiaoHangCell,
                ngayGiaoHang,
                CellType.STRING
        );

        ExcelUtils.setCell(
                diaDiemGiaoHangCell,
                diaDiemGiaoHang,
                CellType.STRING
        );

        ExcelUtils.setCell(
                dieuKhoanThanhToanCell,
                dieuKhoanThanhToan,
                CellType.STRING
        );

        ExcelUtils.setCell(
                ghiChuCell,
                ghiChu,
                CellType.STRING
        );
    }

}
