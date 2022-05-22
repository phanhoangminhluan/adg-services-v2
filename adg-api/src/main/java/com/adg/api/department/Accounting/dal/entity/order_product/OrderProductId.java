package com.adg.api.department.Accounting.dal.entity.order_product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.19 01:29
 */
@Data
@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductId implements Serializable {

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "product_id")
    private int productId;

}
