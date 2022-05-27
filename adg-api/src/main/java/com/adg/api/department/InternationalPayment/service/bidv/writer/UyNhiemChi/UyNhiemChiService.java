package com.adg.api.department.InternationalPayment.service.bidv.writer.UyNhiemChi;

import com.adg.api.department.InternationalPayment.handler.office.word.WordWriter;
import com.adg.api.department.InternationalPayment.service.bidv.NhaCungCapDTO;
import com.adg.api.department.InternationalPayment.service.bidv.enums.HoaDonHeaderMetadata;
import com.adg.api.util.MoneyUtils;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import com.merlin.asset.core.utils.NumberUtils;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.03 15:33
 */
public class UyNhiemChiService {

    private final WordWriter wordWriter;
    private final String outputFolder;
    private final Map<String, Object> data;
    private final ZonedDateTime fileDate;

    public UyNhiemChiService(String outputFolder, Map<String, Object> data, ZonedDateTime fileDate, InputStream inputStream) {
        this.wordWriter = new WordWriter(inputStream, new HashMap<>());
        this.outputFolder = outputFolder;
        this.fileDate = fileDate;
        this.data = this.transformHoaDonRecords(data);
    }

    public Map<String, Object> transformHoaDonRecords(Map<String, Object> hoaDonRecords) {
        Map<String, Object> result = new HashMap<>();

        NhaCungCapDTO nhaCungCapDTO = NhaCungCapDTO.nhaCungCapMap.get(MapUtils.getString(hoaDonRecords, HoaDonHeaderMetadata.NhaCungCap.deAccentedName));
        result.put("Người cung cấp", MapUtils.getString(hoaDonRecords, HoaDonHeaderMetadata.NhaCungCap.deAccentedName));
        result.put("Số tiền bằng số", NumberUtils.formatNumber1(MapUtils.getDouble(hoaDonRecords, HoaDonHeaderMetadata.TongTienThanhToanCacHoaDon.deAccentedName)));
        result.put("Số tài khoản", nhaCungCapDTO == null ?  "" : nhaCungCapDTO.getSoTaiKhoan());
        result.put("Ngân hàng", nhaCungCapDTO == null ?  "" : nhaCungCapDTO.getTenNganHang());
        result.put("Số tiền bằng chữ", MoneyUtils.convertMoneyToText(MapUtils.getDouble(hoaDonRecords, HoaDonHeaderMetadata.TongTienThanhToanCacHoaDon.deAccentedName)));
        result.put("Ngày", DateTimeUtils.convertZonedDateTimeToFormat(this.fileDate, "Asia/Ho_Chi_Minh", DateTimeUtils.getFormatterWithDefaultValue("dd/MM/yyyy")));

        return result;
    }

    public void exportDocument() {
        this.wordWriter.fillTextData(data);
        this.build();
    }

    private void build() {
        String fileName = String.format("Uỷ nhiệm chi - %s - %s.docx",
                MapUtils.getString(this.data, "Người cung cấp"),
                DateTimeUtils.convertZonedDateTimeToFormat(
                        ZonedDateTime.now(),
                        "Asia/Ho_Chi_Minh",
                        DateTimeUtils.FMT_03
                )
        );
        this.wordWriter.build(outputFolder + "/" + fileName);
    }

}
