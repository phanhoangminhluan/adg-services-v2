package com.adg.api.department.InternationalPayment.inventory.dto.order_filter;

import com.adg.api.department.InternationalPayment.inventory.enums.DataType;
import com.adg.api.department.InternationalPayment.inventory.enums.Operator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.21 23:29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterPart {

    @NotBlank
    private String field;

    @NotNull
    @JsonDeserialize(using = DataType.DataTypeDeserializer.class)
    private DataType dataType;

    @NotNull
    @JsonDeserialize(using = Operator.OperatorJsonDeserializer.class)
    private Operator operator;

    @NotNull
    private Object value;

}
