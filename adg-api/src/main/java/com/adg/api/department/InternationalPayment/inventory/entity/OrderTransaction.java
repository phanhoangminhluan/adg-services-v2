package com.adg.api.department.InternationalPayment.inventory.entity;

import com.adg.api.department.InternationalPayment.inventory.entity.shared.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.ZonedDateTime;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 01:30
 */
@Data
@EqualsAndHashCode
@Table(schema = "international_payment", name = "order_transaction")
@Entity
public class OrderTransaction extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;


    @ManyToOne
    @JoinColumn(name = "source_storage_id", referencedColumnName = "id")
    private Storage sourceStorage;

    @ManyToOne
    @JoinColumn(name = "target_storage_id", referencedColumnName = "id")
    private Storage targetStorage;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private OrderTransaction parent;

    private double releaseQuantity;

    private ZonedDateTime releaseDate;

    private String note;

}
