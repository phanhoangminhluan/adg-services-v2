package com.adg.api.department.InternationalPayment.inventory.dto;

import com.adg.api.util.JacksonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.19 07:07
 */
@Data
@Builder
public class LayeredTransactionHistoryDTO {

    private UUID id;
    private UUID orderId;
    private UUID sourceStorageId;
    private UUID targetStorageId;
    private UUID parentId;
    private UUID bankId;
    private double releaseQuantity;
    private List<LayeredTransactionHistoryDTO> children;

    @JsonSerialize(using = JacksonSerializer.ZonedDateToString.class)
    private ZonedDateTime releaseDate;

    private String note;

}
