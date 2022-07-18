package com.adg.api.department.InternationalPayment.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.19 06:10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageDTO {

    private UUID id;
    private String name;

}
