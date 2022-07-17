package com.adg.api.department.InternationalPayment.inventory.dto;

import com.adg.api.department.InternationalPayment.inventory.dto.marker_validator.InsertPurchaseOrderValidator;
import com.adg.api.department.InternationalPayment.inventory.dto.marker_validator.ParseFilePurchaseOrderValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.17 12:00
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilePurchaseOrderDTO {

    @NotNull(groups = {InsertPurchaseOrderValidator.class})
    private UUID portId;

    @NotNull(groups = {InsertPurchaseOrderValidator.class})
    private UUID bankId;

    @NotNull(groups = {
            ParseFilePurchaseOrderValidator.class,
            InsertPurchaseOrderValidator.class
    })
    private List<PurchaseOrderDTO> purchaseOrders;

}
