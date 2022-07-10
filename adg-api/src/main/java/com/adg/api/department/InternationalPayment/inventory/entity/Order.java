package com.adg.api.department.InternationalPayment.inventory.entity;

import com.adg.api.department.InternationalPayment.inventory.dto.DonMuaHangDTO;
import com.adg.api.department.InternationalPayment.inventory.entity.shared.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 01:10
 */

@Data
@EqualsAndHashCode
@Table(
        schema = "international_payment",
        name = "order",
        uniqueConstraints = {@UniqueConstraint(name = "ContractIdAndProductId", columnNames = {"contractId", "productId"})}
)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class Order extends BaseEntity {

    private static final long serialVersionUID = -7815481614875117756L;

    private String contractId;
    private String productId;

    @ManyToOne
    @JoinColumn(name = "port_id", referencedColumnName = "id")
    private Storage port;

    @ManyToOne
    @JoinColumn(name = "bank_id", referencedColumnName = "id")
    private Bank bank;

    private double orderQuantity;

    private double receivedQuantity;

    private double price;

    private ZonedDateTime lcDate;

    private String orderCode;

    private String providerCode;

    private String note;

    public static Order newInstance(DonMuaHangDTO donMuaHangDTO, Bank bank, Storage port) {
        return Order.builder()
                .contractId(donMuaHangDTO.getDienGiai().split("-")[1].trim())
                .productId(donMuaHangDTO.getMaHang())
                .port(port)
                .bank(bank)
                .orderQuantity(donMuaHangDTO.getSoLuongDatHang())
                .receivedQuantity(donMuaHangDTO.getSoLuongDaNhan())
                .price(donMuaHangDTO.getDonGia())
                .lcDate(donMuaHangDTO.getNgayMoLC().atStartOfDay(ZoneId.of("UTC")))
                .orderCode(donMuaHangDTO.getSoDonHang())
                .providerCode(donMuaHangDTO.getMaNhaCungCap())
                .build();

    }

}
