package com.adg.api.department.InternationalPayment.inventory.entity.shared;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.merlin.asset.core.utils.DateTimeUtils;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 01:13
 */
@Data
@JsonPropertyOrder(value = {"id"}, alphabetic = true)
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity {

    private static final long serialVersionUID = 3341548641787004011L;

    @Id
    @Column(unique = true, nullable = false)
    protected UUID id = UUID.randomUUID();
    
    @JsonFormat(pattern = DateTimeUtils.FMT_01)
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = DateTimeUtils.FMT_01)
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    @Version
    @Column(nullable = false)
    private Long version;

    public void setCreatedBy(String createdBy) {
        this.createdBy = Objects.isNull(createdBy) ? "" : createdBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = Objects.isNull(updatedBy) ? "" : updatedBy;
    }

}
