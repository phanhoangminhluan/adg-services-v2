package com.adg.api.department.InternationalPayment.service.bidv.writer.DonCamKet;

import com.adg.api.department.InternationalPayment.general.AdgWordTableHeaderInfo;
import com.adg.api.department.InternationalPayment.handler.office.word.WordUtils;
import com.adg.api.department.InternationalPayment.service.bidv.NhaCungCapDTO;
import com.adg.api.department.InternationalPayment.service.bidv.enums.HoaDonHeaderMetadata;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.NumberUtils;
import com.merlin.asset.core.utils.ParserUtils;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.03 20:16
 */
public enum DonCamKetTableHeaderInfoMetadata implements AdgWordTableHeaderInfo {
    STT(
            "STT",
            0,
            cell -> {
                cell.setWidthType(TableWidthType.PCT);
                cell.setWidth("8.4%");
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                WordUtils.Table.makeCenter(cell);
            },
            runs -> runs.forEach(run -> {
                run.setFontSize(11);
                run.setFontFamily("Times New Roman");
            }),
            record -> MapUtils.getString(record, HoaDonHeaderMetadata.SoThuTuKhongGop.deAccentedName)

    ),
    SoHoaDon(
            "Số hoá đơn",
            1,
            cell -> {
                cell.setWidthType(TableWidthType.PCT);
                cell.setWidth("13.8%");
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                WordUtils.Table.makeCenter(cell);
            },
            runs -> runs.forEach(run -> {
                run.setFontSize(11);
                run.setFontFamily("Times New Roman");
            }),
            record -> MapUtils.getString(record, HoaDonHeaderMetadata.SoHoaDon.deAccentedName)

    ),
    NgayHoaDon(
            "Ngày hoá đơn",
            2,
            cell -> {
                cell.setWidthType(TableWidthType.PCT);
                cell.setWidth("16.3%");
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                WordUtils.Table.makeCenter(cell);
            },
            runs -> runs.forEach(run -> {
                run.setFontSize(11);
                run.setFontFamily("Times New Roman");
            }),
            record -> MapUtils.getString(record, HoaDonHeaderMetadata.NgayChungTu.deAccentedName).split(" ")[0]
    ),
    SoTienTrenHoaDon(
            "Số tiền trên hoá đơn",
            3,
            cell -> {
                cell.setWidthType(TableWidthType.PCT);
                cell.setWidth("21.6%");
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                WordUtils.Table.makeCenter(cell);
            },
            runs -> runs.forEach(run -> {
                run.setFontSize(11);
                run.setFontFamily("Times New Roman");
            }),
            record -> NumberUtils.formatNumber1(ParserUtils.toDouble(MapUtils.getString(record, HoaDonHeaderMetadata.TongTienThanhToan.deAccentedName)))
    ),
    ToChucXuatHoaDon(
            "Tên, mã số thuế của đơn vị, tổ chức xuất hoá đơn",
            4,
            cell -> {
                cell.setWidthType(TableWidthType.PCT);
                cell.setWidth("39.8%");
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                WordUtils.Table.makeCenter(cell);
            },
            runs -> runs.forEach(run -> {
                run.setFontSize(11);
                run.setFontFamily("Times New Roman");
            }),
            record -> {
                String nhaCungCap = MapUtils.getString(record, HoaDonHeaderMetadata.NhaCungCap.deAccentedName);
                NhaCungCapDTO dto = NhaCungCapDTO.nhaCungCapMap.get(nhaCungCap);
                if (dto == null) {
                    return String.format("\nSỐ TK: \nTẠI NH: ");
                }
                return String.format("%s\nSỐ TK: %s\nTẠI NH: %s", dto.getTenKhachHang(), dto.getSoTaiKhoan(), dto.getTenNganHang());
            }
    ),

    ;

    private final String headerName;
    private final int ordinal;
    private final Consumer<XWPFTableCell> cellFormatConsumer;
    private final Consumer<List<XWPFRun>> runConsumer;
    public final Function<Map<String, Object>, String> transformCallback;


    DonCamKetTableHeaderInfoMetadata(String headerName, int ordinal, Consumer<XWPFTableCell> cellFormatConsumer, Consumer<List<XWPFRun>> runConsumer, Function<Map<String, Object>, String> transformCallback) {
        this.headerName = headerName;
        this.ordinal = ordinal;
        this.cellFormatConsumer = cellFormatConsumer;
        this.runConsumer = runConsumer;
        this.transformCallback = transformCallback;
    }

    @Override
    public String getHeaderName() {
        return this.headerName;
    }

    @Override
    public int getOrdinal() {
        return this.ordinal;
    }

    @Override
    public Consumer<XWPFTableCell> cellFormatConsumer() {
        return this.cellFormatConsumer;
    }

    @Override
    public Consumer<List<XWPFRun>> runConsumer() {
        return this.runConsumer;
    }
}
