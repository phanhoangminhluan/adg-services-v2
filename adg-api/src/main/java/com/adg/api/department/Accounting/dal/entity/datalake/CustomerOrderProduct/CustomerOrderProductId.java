package com.adg.api.department.Accounting.dal.entity.datalake.CustomerOrderProduct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.06.13 23:33
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerOrderProductId implements Serializable {

    private String customerId;
    private String orderId;
    private String productId;

}
