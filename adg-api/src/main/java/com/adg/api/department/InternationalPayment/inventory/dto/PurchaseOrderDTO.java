package com.adg.api.department.InternationalPayment.inventory.dto;

import com.adg.api.util.JacksonDeserializer;
import com.adg.api.util.JacksonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 01:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderDTO {

    @NotBlank
    private String orderCode;

    @NotBlank
    private String providerCode;

    @NotBlank
    private String productId;

    @JsonDeserialize(using = JacksonDeserializer.StringToDouble.class)
    @Min(value = 1)
    private double orderQuantity;

    @JsonDeserialize(using = JacksonDeserializer.StringToDouble.class)
    @Min(value = 0)
    private double receivedQuantity;

    @JsonDeserialize(using = JacksonDeserializer.StringToDouble.class)
    @Min(value = 1)
    private double unitPrice;

    @JsonDeserialize(using = JacksonDeserializer.StringToLocalDate.class)
    @JsonSerialize(using = JacksonSerializer.LocalDateToString.class)
    private LocalDate lcDate;

    @NotBlank
    private String status;

    @NotBlank
    private String description;

}
