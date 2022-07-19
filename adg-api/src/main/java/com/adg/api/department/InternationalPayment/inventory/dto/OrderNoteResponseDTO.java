package com.adg.api.department.InternationalPayment.inventory.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.19 21:58
 */
@Data
@Builder
public class OrderNoteResponseDTO {

    private List<UUID> successIDs;
    private Map<UUID, String> errorIdWithReason;

}

