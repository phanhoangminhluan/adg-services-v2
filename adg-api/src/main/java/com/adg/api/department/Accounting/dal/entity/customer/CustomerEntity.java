package com.adg.api.department.Accounting.dal.entity.customer;

import com.adg.api.department.Accounting.dal.entity.BaseEntity;
import com.merlin.mapper.annotations.MappingField;
import com.merlin.mapper.annotations.SourceFieldConfig;
import com.merlin.mapper.annotations.TargetFieldConfig;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.12 15:25
 */
@Data
@Entity
@Table(name = "customer", schema = "public")
public class CustomerEntity extends BaseEntity implements Serializable{

    @Id
    @MappingField
    private String asyncId;

    @Column
    @MappingField
    private int id;

    @Column
    @MappingField
    private String accountName;

    @Column
    @MappingField
    private String accountNumber;

    @Column
    @MappingField
    private String accountShortName;

    @Column
    @MappingField
    private String bankAccount;

    @Column
    @MappingField
    private String bankName;

    @Column
    @MappingField
    @Type(type = "text")
    private String billingAddress;

    @Column
    @MappingField
    private String billingCountry;

    @Column
    @MappingField
    private String billingProvince;

    @Column
    @MappingField
    private String budgetCode;

    @Column
    @MappingField
    @Type(type = "text")
    private String description;

    @Column
    @MappingField
    private String fax;

    @Column
    @MappingField
    private int formLayoutId;

    @Column
    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setPublic")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isPublic")
            }
    )
    private boolean isPublic;;

    @Column
    @MappingField
    private String officeEmail;

    @Column
    @MappingField
    private String officeTel;

    @Column
    @MappingField
    private int organizationUnitId;

    @Column
    @MappingField
    private String organizationUnitName;

    @Column
    @MappingField
    private int ownerId;

    @Column
    @MappingField
    private String ownerName;

    @Column
    @MappingField
    private String parentAccountName;

    @Column
    @MappingField
    @Type(type = "text")
    private String shippingAddress;

    @Column
    @MappingField
    private String shippingCountry;

    @Column
    @MappingField
    private String taxCode;

    @Column
    @MappingField
    private String website;
}