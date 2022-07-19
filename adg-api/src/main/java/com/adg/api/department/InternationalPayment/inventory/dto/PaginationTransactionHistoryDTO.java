package com.adg.api.department.InternationalPayment.inventory.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.19 07:57
 */
@Data
@Builder
public class PaginationTransactionHistoryDTO {

    private List<TransactionHistoryDTO> transactions;
    private int totalPages;
    private int totalRecords;

}
