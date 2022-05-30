package com.adg.api.department.InternationalPayment.handler.office;

import com.adg.api.department.InternationalPayment.service.bidv.writer.BangKeSuDungTienVay.BangKeSuDungTienVayHeaderInfoMetadata;
import com.adg.api.department.InternationalPayment.service.bidv.writer.DonMuaHang.DonMuaHangHeaderInfoMetadata;
import com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeChungTuDeNghiGiaiNgan.BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo;
import com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeNopThue.BangKeNopThueHeaderInfoMetadata;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.24 14:18
 */
@Data
@Builder
public class AdgExcelTableHeaderMetadata {

    private List<AdgExcelTableHeaderInfo> headers;
    private String startCellAddress;
    private int columnSize;

    public static AdgExcelTableHeaderMetadata getBangKeChungTuDienTuDeNghiGiaiNgan() {
        List<AdgExcelTableHeaderInfo> headers = Arrays.asList(BangKeChungTuDienTuDeNghiGiaiNganMetadataHeaderInfo.values());

        return AdgExcelTableHeaderMetadata.builder()
                .headers(headers)
                .startCellAddress("A10")
                .columnSize(headers.size())
                .build();
    }

    public static AdgExcelTableHeaderMetadata getBidvBangKeSuDungTienVay() {
        List<AdgExcelTableHeaderInfo> headers = Arrays.asList(BangKeSuDungTienVayHeaderInfoMetadata.values());

        return AdgExcelTableHeaderMetadata.builder()
                .headers(headers)
                .startCellAddress("A10")
                .columnSize(headers.size())
                .build();
    }

    public static AdgExcelTableHeaderMetadata getViettinBangKeSuDungTienVay() {
        List<AdgExcelTableHeaderInfo> headers = Arrays.asList(com.adg.api.department.InternationalPayment.service.viettin.writer.BangKeSuDungTienVay.BangKeSuDungTienVayHeaderInfoMetadata.values());

        return AdgExcelTableHeaderMetadata.builder()
                .headers(headers)
                .startCellAddress("A7")
                .columnSize(headers.size())
                .build();
    }

    public static AdgExcelTableHeaderMetadata getBangKeNopThue() {
        List<AdgExcelTableHeaderInfo> headers = Arrays.asList(BangKeNopThueHeaderInfoMetadata.values());

        return AdgExcelTableHeaderMetadata.builder()
                .headers(headers)
                .startCellAddress("D26")
                .columnSize(headers.size())
                .build();
    }

    public static AdgExcelTableHeaderMetadata getDonMuaHang() {
        List<AdgExcelTableHeaderInfo> headers = Arrays.asList(DonMuaHangHeaderInfoMetadata.values());
        return AdgExcelTableHeaderMetadata.builder()
                .headers(headers)
                .startCellAddress("A15")
                .columnSize(headers.size())
                .build();
    }

}
