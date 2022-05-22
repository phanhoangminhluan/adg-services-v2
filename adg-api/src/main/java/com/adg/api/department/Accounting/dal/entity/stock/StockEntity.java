package com.adg.api.department.Accounting.dal.entity.stock;

import com.adg.api.department.Accounting.dal.entity.BaseEntity;
import com.merlin.mapper.annotations.MappingField;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.08 16:47
 */
@Data
@Entity
@Table(name = "stock", schema = "public")
public class StockEntity extends BaseEntity {

    @MappingField
    @Id
    @Column(name = "async_id")
    private String asyncId;

    @MappingField
    @Column(name = "act_database_id")
    private String actDatabaseId;

    @MappingField
    @Column(name = "inactive")
    private boolean inactive;

    @MappingField
    @Column(name = "stock_code")
    private String stockCode;

    @MappingField
    @Column(name = "stock_name")
    private String stockName;

}
