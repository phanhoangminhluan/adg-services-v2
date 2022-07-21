package com.adg.api.department.InternationalPayment.inventory.dto.order_filter;

import com.adg.api.department.InternationalPayment.inventory.enums.SortDirection;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.21 23:25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilter {

    @NotNull
    private UUID bankId;

    @NotNull
    private UUID portId;

    @NotNull
    private String sortField;

    @NotNull
    @JsonDeserialize(using = SortDirection.SortDirectionJsonDeserializer.class)
    private SortDirection sortDirection;

    @NotNull
    @Valid
    private Filters filters;

    private int pageLimit;

    private int pageSize;
}
