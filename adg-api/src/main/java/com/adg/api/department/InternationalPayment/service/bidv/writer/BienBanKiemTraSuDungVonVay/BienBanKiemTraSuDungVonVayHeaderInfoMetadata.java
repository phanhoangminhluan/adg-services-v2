package com.adg.api.department.InternationalPayment.service.bidv.writer.BienBanKiemTraSuDungVonVay;

import com.adg.api.department.InternationalPayment.handler.office.AdgWordTableHeaderInfo;
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
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.09 22:26
 */
public enum BienBanKiemTraSuDungVonVayHeaderInfoMetadata implements AdgWordTableHeaderInfo {
    TT(
        "TT",
        0,
        cell -> {
            cell.setWidthType(TableWidthType.PCT);
            cell.setWidth("7.5%");
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            WordUtils.Table.makeCenter(cell);
        },
        runs -> runs.forEach(run -> {
            run.setFontSize(11);
            run.setFontFamily("Times New Roman");
        }),
        record -> MapUtils.getString(record, HoaDonHeaderMetadata.SoThuTuCoGop.deAccentedName)
    ),
    NoiDung(
            "Nội dung",
            1,
            cell -> {
                cell.setWidthType(TableWidthType.PCT);
                cell.setWidth("19.7%");
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                WordUtils.Table.makeCenter(cell);
            },
            runs -> runs.forEach(run -> {
                run.setFontSize(11);
                run.setFontFamily("Times New Roman");
            }),
            record -> {
                List<String> listSoHoaDon =
                        MapUtils.getListString(record, HoaDonHeaderMetadata.ListSoHoaDon.deAccentedName)
                                .stream()
                                .map(soHoaDon -> "0" + soHoaDon.replace("0", ""))
                                .collect(Collectors.toList());
                ;
                return String.format("Thanh toán tiền hàng theo hoá đơn %s hết.", String.join(", ", listSoHoaDon));
            }

    ),
    SoHieuChungTuKeToan(
            "Số hiệu chứng từ kế toán",
            2,
            cell -> {
                cell.setWidthType(TableWidthType.PCT);
                cell.setWidth("20%");
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                WordUtils.Table.makeCenter(cell);
            },
            runs -> runs.forEach(run -> {
                run.setFontSize(11);
                run.setFontFamily("Times New Roman");
            }),
            record -> "UNC"
    ),
    SoTienVND(
            "Số tiền (VND)",
            3,
            cell -> {
                cell.setWidthType(TableWidthType.PCT);
                cell.setWidth("20%");
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                WordUtils.Table.makeCenter(cell);
            },
            runs -> runs.forEach(run -> {
                run.setFontSize(11);
                run.setFontFamily("Times New Roman");
            }),
            record -> NumberUtils.formatNumber1(ParserUtils.toDouble(MapUtils.getString(record, HoaDonHeaderMetadata.TongTienThanhToanCacHoaDon.deAccentedName)))
    ),
    TenDonVi(
            "Tên đơn vị, số tài khoản, Ngân hàng người thụ hưởng",
            4,
            cell -> {
                cell.setWidthType(TableWidthType.PCT);
                cell.setWidth("32.7%");
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
    )
    ;

    private final String headerName;
    private final int ordinal;
    private final Consumer<XWPFTableCell> cellFormatConsumer;
    private final Consumer<List<XWPFRun>> runConsumer;
    public final Function<Map<String, Object>, String> transformCallback;


    BienBanKiemTraSuDungVonVayHeaderInfoMetadata(String headerName, int ordinal, Consumer<XWPFTableCell> cellFormatConsumer, Consumer<List<XWPFRun>> runConsumer, Function<Map<String, Object>, String> transformCallback) {
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
