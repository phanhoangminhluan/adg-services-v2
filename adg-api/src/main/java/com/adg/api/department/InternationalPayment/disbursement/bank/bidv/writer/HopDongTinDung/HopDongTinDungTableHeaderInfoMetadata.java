package com.adg.api.department.InternationalPayment.disbursement.bank.bidv.writer.HopDongTinDung;

import com.adg.api.department.InternationalPayment.disbursement.NhaCungCapDTO;
import com.adg.api.department.InternationalPayment.disbursement.office.AdgWordTableHeaderInfo;
import com.adg.api.department.InternationalPayment.disbursement.office.word.WordUtils;
import com.adg.api.department.InternationalPayment.disbursement.reader.header.HoaDonHeaderMetadata;
import com.adg.api.department.InternationalPayment.disbursement.reader.service.HoaDonService;
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
 * Created on: 2022.05.02 10:24
 */
public enum HopDongTinDungTableHeaderInfoMetadata implements AdgWordTableHeaderInfo {
    STT(
            "STT",
            0,
            cell -> {
                cell.setWidthType(TableWidthType.PCT);
                cell.setWidth("7.1%");
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                WordUtils.Table.makeCenter(cell);
            },
            runs -> runs.forEach(run -> run.setFontSize(11)),
            record -> MapUtils.getString(record, HoaDonHeaderMetadata.SoThuTuCoGop.deAccentedName)
    ),
    NoiDung(
            "Nội dung",
            1,
            cell -> {
                cell.setWidthType(TableWidthType.PCT);
                cell.setWidth("18.1%");
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                WordUtils.Table.makeCenter(cell);

            },
            runs -> runs.forEach(run -> run.setFontSize(11)),
            record -> {
                List<String> listSoHoaDon = MapUtils
                        .getListString(record, HoaDonHeaderMetadata.ListSoHoaDon.deAccentedName)
                        .stream()
                        .map(HoaDonService::transformSoHoaDon)
                        .collect(Collectors.toList());
                return String.format("Thanh toán tiền hàng theo hoá đơn %s hết.", String.join(", ", listSoHoaDon));
            }

    ),
    SoHieuChungTuKeToan(
            "Số hiệu chứng từ kế toán",
            2,
            cell -> {
                cell.setWidthType(TableWidthType.PCT);
                cell.setWidth("12.6%");
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                WordUtils.Table.makeCenter(cell);

            },
            runs -> runs.forEach(run -> run.setFontSize(11)),
            record -> "UNC"
    ),
    SoTienVND(
            "Số tiền (VND)",
            3,
            cell -> {
                cell.setWidthType(TableWidthType.PCT);
                cell.setWidth("23.1%");
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                WordUtils.Table.makeCenter(cell);

            },
            runs -> runs.forEach(run -> run.setFontSize(11)),
            record -> NumberUtils.formatNumber1(ParserUtils.toDouble(MapUtils.getString(record, HoaDonHeaderMetadata.TongTienThanhToanCacHoaDon.deAccentedName)))
    ),
    TenDonViSoTaiKhoan(
            "Tên đơn vị, số tài khoản, Ngân hàng người thụ hưởng",
            4,
            cell -> {
                cell.setWidthType(TableWidthType.PCT);
                cell.setWidth("38.9%");
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                WordUtils.Table.makeCenter(cell);

            },
            runs -> runs.forEach(run -> run.setFontSize(11)),
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


    HopDongTinDungTableHeaderInfoMetadata(String headerName, int ordinal, Consumer<XWPFTableCell> cellFormatConsumer, Consumer<List<XWPFRun>> runConsumer, Function<Map<String, Object>, String> transformCallback) {
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
