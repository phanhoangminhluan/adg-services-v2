package com.adg.api.department.Accounting.dal.entity.product;

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

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.12 15:38
 */
@Data
@Entity
@Table(name = "product", schema = "public")
public class ProductEntity extends BaseEntity {

    @Id
    @MappingField
    private String asyncId;

    @Column
    @MappingField
    private int id;

    @Column
    @MappingField
    private double conversionRate;

    @Column
    @MappingField
    @Type(type = "text")
    private String description;

    @Column
    @MappingField
    private int formLayoutId;

    @Column
    @MappingField
    private boolean inactive;

    @Column
    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setDeleted")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isDeleted")
            }
    )
    private boolean isDeleted;

    @Column
    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setFollowSerialNumber")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isFollowSerialNumber")
            }
    )
    private boolean isFollowSerialNumber;

    @Column
    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setPublic")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isPublic")
            }
    )
    private boolean isPublic;

    @Column
    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setUseTax")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isUseTax")
            }
    )
    private boolean isUseTax;

    @Column
    @MappingField
    private int ownerId;

    @Column
    @MappingField
    private int organizationUnitId;

    @Column
    @MappingField
    private String ownerName;

    @Column
    @MappingField
    private boolean priceAfterTax;

    @Column
    @MappingField
    private String productCategoryName;

    @Column
    @MappingField
    private String productCode;

    @Column
    @MappingField
    private String productName;

    @Column
    @MappingField
    private double purchasedPrice;

    @Column
    @MappingField
    private double quantityDemanded;

    @Column
    @MappingField
    private double quantityInstock;

    @Column
    @MappingField
    private double quantityOrdered;

    @Column
    @MappingField
    private boolean taxable;

    @Column
    @MappingField
    private String taxIdText;

    @Column
    @MappingField
    private double unitCost;

    @Column
    @MappingField
    private double unitPrice;

    @Column
    @MappingField
    private double unitPrice1;

    @Column
    @MappingField
    private double unitPrice2;

    @Column
    @MappingField
    private double unitPriceFixed;

    @Column
    @MappingField
    private String usageUnitIdText;

    @Column
    @MappingField
    private int vendorNameId;

}
