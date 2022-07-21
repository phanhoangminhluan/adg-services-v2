package com.adg.api.department.InternationalPayment.inventory.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.10 23:27
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetOrderDTO {

    private List<OrderDTO> orders;
    private int totalPages;
    private long totalRecords;

}
