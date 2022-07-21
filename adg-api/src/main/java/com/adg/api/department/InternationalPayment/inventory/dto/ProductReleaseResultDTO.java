package com.adg.api.department.InternationalPayment.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.22 02:04
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductReleaseResultDTO {

    private int stockedProducts;

}
