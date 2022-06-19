package com.adg.api.department.InternationalPayment.handler.office;

import com.adg.api.department.InternationalPayment.bank.bidv.writer.BienBanKiemTraSuDungVonVay.BienBanKiemTraSuDungVonVayHeaderInfoMetadata;
import com.adg.api.department.InternationalPayment.bank.bidv.writer.DonCamKet.DonCamKetTableHeaderInfoMetadata;
import com.adg.api.department.InternationalPayment.bank.bidv.writer.HopDongTinDung.HopDongTinDungTableHeaderInfoMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.02 23:47
 */
public class AdgWordTableHeaderMetadata {

    public static Map<Integer, AdgWordTableHeaderInfo> getHeaderMapHopDongTinDung() {
        Map<Integer, AdgWordTableHeaderInfo> headerMap = new HashMap<>();
        for (HopDongTinDungTableHeaderInfoMetadata value : HopDongTinDungTableHeaderInfoMetadata.values()) {
            headerMap.put(value.getOrdinal(), value);
        }
        return headerMap;
    }

    public static Map<Integer, AdgWordTableHeaderInfo> getHeaderMapDonCamKet() {
        Map<Integer, AdgWordTableHeaderInfo> headerMap = new HashMap<>();

        for (DonCamKetTableHeaderInfoMetadata value : DonCamKetTableHeaderInfoMetadata.values()) {
            headerMap.put(value.getOrdinal(), value);
        }
        return headerMap;
    }

    public static Map<Integer, AdgWordTableHeaderInfo> getHeaderBienBanKiemTraSuDungVonVay() {
        Map<Integer, AdgWordTableHeaderInfo> headerMap = new HashMap<>();
        for (BienBanKiemTraSuDungVonVayHeaderInfoMetadata value : BienBanKiemTraSuDungVonVayHeaderInfoMetadata.values()) {
            headerMap.put(value.getOrdinal(), value);
        }
        return headerMap;
    }

}
