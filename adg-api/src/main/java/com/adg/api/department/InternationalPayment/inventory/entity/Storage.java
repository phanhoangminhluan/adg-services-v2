package com.adg.api.department.InternationalPayment.inventory.entity;

import com.adg.api.department.InternationalPayment.inventory.entity.shared.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 01:22
 */
@Data
@EqualsAndHashCode
@Table(schema = "international_payment", name = "storage")
@Entity
public class Storage extends BaseEntity {

    private String name;

    @Column(columnDefinition = "boolean default false")
    private boolean isPort;

}
