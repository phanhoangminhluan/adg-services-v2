package com.adg.api.department.Accounting.dal.entity.organization_unit;

import com.adg.api.department.Accounting.dal.entity.BaseEntity;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.mapper.annotations.MappingField;
import com.merlin.mapper.annotations.SourceFieldConfig;
import com.merlin.mapper.annotations.TargetFieldConfig;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.12 15:25
 */
@Data
@Entity
@Table(name = "organization_unit", schema = "public")
public class OrganizationUnitEntity extends BaseEntity implements Serializable{

    @Id
    @MappingField
    public int id;

    @Column(columnDefinition = "text")
    @MappingField
    public String organizationUnitCode;

    @Column(columnDefinition = "text")
    @MappingField
    public String organizationUnitName;

    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setInactive")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isInactive")
            }
    )
    public boolean inactive;

    @Column(columnDefinition = "text")
    @MappingField
    public String misaCode;

    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setSystem")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isSystem")
            }
    )
    public boolean isSystem;

    @MappingField
    public int parentId;

    @Column(columnDefinition = "text")
    @MappingField
    public String organizationUnitTypeId;

    @Column(columnDefinition = "text")
    @MappingField
    public String organizationUnitTypeName;

    @MappingField(targetFieldConfigs = {
            @TargetFieldConfig(targetSetterMethod = "formatThenSetCreatedDate")
    })
    public ZonedDateTime createdDate;

    @MappingField(targetFieldConfigs = {
            @TargetFieldConfig(targetSetterMethod = "formatThenSetModifiedDate")
    })
    public ZonedDateTime modifiedDate;

    @Column(columnDefinition = "text")
    @MappingField
    public String asyncId;

    @MappingField
    public int branchId;

    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setProductive")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isProductive")
            }
    )
    public boolean isProductive;

    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setBackOffice")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isBackOffice")
            }
    )
    public boolean isBackOffice;

    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setSupport")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isSupport")
            }
    )
    public boolean isSupport;

    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setSale")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isSale")
            }
    )
    public boolean isSale;

    public void formatThenSetCreatedDate(String rawValue) {
        this.createdDate = DateTimeUtils.convertStringToZonedDateTime(rawValue, DateTimeUtils.ISO8601_DATE_TIME_FORMATTER,  "Asia/Ho_Chi_Minh", "Asia/Ho_Chi_Minh");
    }

    public void formatThenSetModifiedDate(String rawValue) {
        this.modifiedDate = DateTimeUtils.convertStringToZonedDateTime(rawValue, DateTimeUtils.ISO8601_DATE_TIME_FORMATTER,  "Asia/Ho_Chi_Minh", "Asia/Ho_Chi_Minh");
    }
}