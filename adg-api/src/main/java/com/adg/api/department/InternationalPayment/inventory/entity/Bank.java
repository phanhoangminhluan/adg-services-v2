package com.adg.api.department.InternationalPayment.inventory.entity;

import com.adg.api.department.InternationalPayment.inventory.entity.shared.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 01:19
 */
@Data
@EqualsAndHashCode
@Table(schema = "international_payment", name = "bank")
@Entity
public class Bank extends BaseEntity {

    private String name;
    private String otherName;

}
