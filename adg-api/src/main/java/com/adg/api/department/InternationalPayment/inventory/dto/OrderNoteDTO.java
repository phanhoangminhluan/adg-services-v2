package com.adg.api.department.InternationalPayment.inventory.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.19 22:17
 */
@Data
@Builder
public class OrderNoteDTO {
    @NotNull
    private UUID orderId;
    @NotBlank
    @Size(min = 1, max = 256)
    private String note;
}
