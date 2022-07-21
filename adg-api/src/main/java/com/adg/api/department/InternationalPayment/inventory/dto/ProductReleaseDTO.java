package com.adg.api.department.InternationalPayment.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.22 01:59
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductReleaseDTO {

    @Min(1)
    private int releaseQuantity;

    @Nullable
    private UUID targetStorageId;

    @Nullable
    private String note;

}
