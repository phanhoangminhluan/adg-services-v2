package com.adg.api.department.Accounting.dal.entity;

import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.mapper.annotations.MappingField;
import com.merlin.mapper.annotations.TargetFieldConfig;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.19 02:33
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @MappingField
    @Column
    protected String createdBy;

    @MappingField(targetFieldConfigs = {
            @TargetFieldConfig(
                    targetSetterMethod = "formatThenSetCreatedDate"
            ),
    })
    @Column
    protected ZonedDateTime createdDate;

    @MappingField
    @Column
    protected String modifiedBy;

    @Column
    @CreatedDate
    protected LocalDateTime internalCreatedDate;

    @Column
    @LastModifiedDate
    protected LocalDateTime internalLastModifiedDate;

    @MappingField(targetFieldConfigs = {
            @TargetFieldConfig(
                    targetSetterMethod = "formatThenSetModifiedDate"
            ),
    })
    @Column
    protected ZonedDateTime modifiedDate;


    public void formatThenSetCreatedDate(String rawValue) {
        this.createdDate = DateTimeUtils.convertStringToZonedDateTime(rawValue, DateTimeUtils.ISO8601_DATE_TIME_FORMATTER,  "Asia/Ho_Chi_Minh", "Asia/Ho_Chi_Minh");
    }

    public void formatThenSetModifiedDate(String rawValue) {
        this.modifiedDate = DateTimeUtils.convertStringToZonedDateTime(rawValue, DateTimeUtils.ISO8601_DATE_TIME_FORMATTER,  "Asia/Ho_Chi_Minh", "Asia/Ho_Chi_Minh");
    }

}
