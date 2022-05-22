package com.adg.api.department.Accounting.dal.entity.employee;

import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.mapper.annotations.MappingField;
import com.merlin.mapper.annotations.SourceFieldConfig;
import com.merlin.mapper.annotations.TargetFieldConfig;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.01 22:51
 */
@Data
@Entity
@Table(name = "employee", schema = "public")
public class EmployeeEntity {

    @Id
    @MappingField
    public int id;

    @MappingField
    @Column(columnDefinition = "text")
    public String employeeCode;

    @MappingField
    @Column(columnDefinition = "text")
    public String fullName;

    @MappingField
    @Column(columnDefinition = "text")
    public String firstName;

    @MappingField
    @Column(columnDefinition = "text")
    public String lastName;

    @MappingField
    public int organizationUnitId;

    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setApproved")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isApproved")
            }
    )
    public boolean isApproved;

    @MappingField
    @Column(columnDefinition = "text")
    public String email;

    @MappingField
    @Column(columnDefinition = "text")
    public String officeEmail;

    @MappingField
    public String mobile;

    @MappingField
    public boolean active;

    @MappingField
    @Column(columnDefinition = "text")
    public String createdBy;

    @MappingField(targetFieldConfigs = {
            @TargetFieldConfig(targetSetterMethod = "formatThenSetCreatedDate")
    })
    public ZonedDateTime createdDate;

    @MappingField
    @Column(columnDefinition = "text")
    public String modifiedBy;

    @MappingField(targetFieldConfigs = {
            @TargetFieldConfig(targetSetterMethod = "formatThenSetModifiedDate")
    })
    public ZonedDateTime modifiedDate;

    @MappingField
    @Column(columnDefinition = "text")
    public String asyncId;

    public void formatThenSetCreatedDate(String rawValue) {
        this.createdDate = DateTimeUtils.convertStringToZonedDateTime(rawValue, DateTimeUtils.ISO8601_DATE_TIME_FORMATTER,  "Asia/Ho_Chi_Minh", "Asia/Ho_Chi_Minh");
    }

    public void formatThenSetModifiedDate(String rawValue) {
        this.modifiedDate = DateTimeUtils.convertStringToZonedDateTime(rawValue, DateTimeUtils.ISO8601_DATE_TIME_FORMATTER,  "Asia/Ho_Chi_Minh", "Asia/Ho_Chi_Minh");
    }
}
