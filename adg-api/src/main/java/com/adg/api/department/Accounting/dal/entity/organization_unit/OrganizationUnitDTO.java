package com.adg.api.department.Accounting.dal.entity.organization_unit;

import com.adg.api.department.Accounting.service.AbstractDTO;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.11 15:47
 */
@Data
public class OrganizationUnitDTO extends AbstractDTO {

    @SerializedName("id")
    public int id;

    @SerializedName("organization_unit_code")
    public String organizationUnitCode;

    @SerializedName("organization_unit_name")
    public String organizationUnitName;

    @SerializedName("inactive")
    public boolean inactive;

    @SerializedName("misa_code")
    public String misaCode;

    @SerializedName("is_system")
    public boolean isSystem;

    @SerializedName("parent_id")
    public int parentId;

    @SerializedName("organization_unit_type_id")
    public String organizationUnitTypeId;

    @SerializedName("organization_unit_type_name")
    public String organizationUnitTypeName;

    @SerializedName("created_date")
    public String createdDate;

    @SerializedName("modified_date")
    public String modifiedDate;

    @SerializedName("async_id")
    public String asyncId;

    @SerializedName("branch_id")
    public int branchId;

    @SerializedName("is_productive")
    public boolean isProductive;

    @SerializedName("is_back_office")
    public boolean isBackOffice;

    @SerializedName("is_support")
    public boolean isSupport;

    @SerializedName("is_sale")
    public boolean isSale;
}
