package com.adg.api.department.Accounting.dal.entity.stock;

import com.adg.api.department.Accounting.service.AbstractDTO;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.08 16:47
 */
@Data
@Builder
public class StockDTO extends AbstractDTO {


    @SerializedName("act_database_id")
    private String actDatabaseId;

    @SerializedName("async_id")
    private String asyncId;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("created_date")
    private String createdDate;

    @SerializedName("inactive")
    private boolean inactive;

    @SerializedName("modified_by")
    private String modifiedBy;

    @SerializedName("modified_date")
    private String modifiedDate;

    @SerializedName("stock_code")
    private String stockCode;

    @SerializedName("stock_name")
    private String stockName;
}
