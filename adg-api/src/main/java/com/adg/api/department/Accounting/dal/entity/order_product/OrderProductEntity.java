package com.adg.api.department.Accounting.dal.entity.order_product;

import com.adg.api.department.Accounting.dal.entity.BaseEntity;
import com.adg.api.department.Accounting.dal.entity.order.OrderEntity;
import com.merlin.mapper.annotations.MappingField;
import com.merlin.mapper.annotations.SourceFieldConfig;
import com.merlin.mapper.annotations.TargetFieldConfig;
import lombok.Data;

import javax.persistence.*;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.12 15:22
 */
@Data
@Entity
@Table(name = "order_product", schema = "public")
public class OrderProductEntity extends BaseEntity {

    @EmbeddedId
    private OrderProductId orderProductId;

    @ManyToOne(
            cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE},
            fetch = FetchType.LAZY
    )
//    @MapsId("orderId")
//    @NotFound(action = NotFoundAction.IGNORE)
//    @JoinColumn(
//            name = "order_id",
//            nullable = true
//    )
    @Transient
    private OrderEntity orderEntity;

    @Column
    @MappingField
    private String unit;

    @Column
    @MappingField
    private double price;

    @Column
    @MappingField
    private double amount;

    @Column
    @MappingField
    private double toCurrency;

    @Column
    @MappingField
    private double discount;

    @Column
    @MappingField
    private double tax;

    @Column
    @MappingField
    private double total;

    @Column
    @MappingField
    private String productName;

    @Column
    @MappingField
    private String productCode;

    @Column
    @MappingField
    private String taxPercent;

    @Column
    @MappingField
    private double discountPercent;

    @Column
    @MappingField
    private double shippingAmount;

    @Column
    @MappingField
    private double usageUnitAmount;

    @Column
    @MappingField
    private String description;

    @Column
    @MappingField
    private double toCurrencyOc;

    @Column
    @MappingField
    private double discountOc;

    @Column
    @MappingField
    private double taxOc;

    @Column
    @MappingField
    private double totalOc;

    @Column
    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setDiscountDirectly")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isDiscountDirectly")
            }
    )
    private boolean isDiscountDirectly;

    @Column
    @MappingField
    private double priceAfterTax;

    @Column
    @MappingField
    private int sortOrder;

    @Transient
    @MappingField(sourceFieldConfigs = {
            @SourceFieldConfig(sourceGetterMethod = "getProductId")
    })
    private int tempProductId;

    public OrderProductEntity() {

    }

}
