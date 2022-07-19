package com.adg.api.department.InternationalPayment.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.19 21:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderNoteRequestDTO {

    @NotEmpty
    private List<OrderNoteDTO> orders;

}

