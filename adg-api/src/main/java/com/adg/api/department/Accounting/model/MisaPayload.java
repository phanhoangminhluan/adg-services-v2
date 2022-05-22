package com.adg.api.department.Accounting.model;

import com.merlin.asset.core.utils.MapUtils;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.21 22:23
 */
@Data
@Getter
@Setter
@Builder
public class MisaPayload {


    private int pageSize;
    private int totalPages;
    private int totalRecords;
    private int code;
    private List<Map<String, Object>> data;

    public static MisaPayload toPayload(Map<String, Object> map) {
        List<Map<String, Object>> data = MapUtils.getListMapStringObject(map, "data");
        return MisaPayload.builder()
                .pageSize(MapUtils.getInt(map, "page_size", data.size()))
                .totalPages(MapUtils.getInt(map, "total_pages", data.size() > 0 ? 1 : 0))
                .totalRecords(MapUtils.getInt(map, "total_records", data.size()))
                .data(data)
                .build();
    }
}
