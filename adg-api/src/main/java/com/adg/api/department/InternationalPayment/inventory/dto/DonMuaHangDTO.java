package com.adg.api.department.InternationalPayment.inventory.dto;

import com.adg.api.util.JacksonDeserializer;
import com.adg.api.util.JacksonSerializer;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 01:53
 */
@JsonPropertyOrder(value = {
        "nganHangMoLC",
        "soDonHang",
        "maNhaCungCap",
        "maHang",
        "soLuongDatHang",
        "soLuongDaNhan",
        "soLuongConLai",
        "donGia",
        "giaTriDatHang",
        "ngayMoLc",
        "tinhTrang",
        "duKienHangVe",
        "dienGiai",
        "khoDangKy",
        "hanThanhToan",
        "daThanhToan",
        "hopDongMua",
        "hanLuuCont",
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonMuaHangDTO {

    private String nganHangMoLC;
    private String soDonHang;
    private String maNhaCungCap;
    private String maHang;

    @JsonDeserialize(using = JacksonDeserializer.StringToDouble.class)
    private double soLuongDatHang;

    @JsonDeserialize(using = JacksonDeserializer.StringToDouble.class)
    private double soLuongDaNhan;

    @JsonDeserialize(using = JacksonDeserializer.StringToDouble.class)
    private double soLuongConLai;

    @JsonDeserialize(using = JacksonDeserializer.StringToDouble.class)
    private double donGia;

    @JsonDeserialize(using = JacksonDeserializer.StringToDouble.class)
    private double giaTriDatHang;

    @JsonDeserialize(using = JacksonDeserializer.StringToLocalDate.class)
    @JsonSerialize(using = JacksonSerializer.LocalDateToString.class)
    private LocalDate ngayMoLC;

    private String tinhTrang;

    @JsonDeserialize(using = JacksonDeserializer.StringToLocalDate.class)
    @JsonSerialize(using = JacksonSerializer.LocalDateToString.class)
    private LocalDate duKienHangVe;

    private String dienGiai;
    private String khoDangKy;
    private String hanThanhToan;
    private String daThanhToan;
    private String hopDongMua;
    private String hanLuuCont;

}
