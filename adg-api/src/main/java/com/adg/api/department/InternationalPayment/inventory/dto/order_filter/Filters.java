package com.adg.api.department.InternationalPayment.inventory.dto.order_filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.21 23:28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Filters {

    @NotNull
    @Valid
    private List<FilterPart> and;

}
