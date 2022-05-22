package com.adg.api.department.Accounting.dal.entity.employee;

import com.adg.api.department.Accounting.service.AbstractDTO;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.01 22:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO extends AbstractDTO {

    @SerializedName("id")
    public int id;

    @SerializedName("employee_code")
    public String employeeCode;

    @SerializedName("full_name")
    public String fullName;

    @SerializedName("first_name")
    public String firstName;

    @SerializedName("last_name")
    public String lastName;

    @SerializedName("organization_unit_id")
    public int organizationUnitId;

    @SerializedName("is_approved")
    public boolean isApproved;

    @SerializedName("email")
    public String email;

    @SerializedName("office_email")
    public String officeEmail;

    @SerializedName("mobile")
    public String mobile;

    @SerializedName("active")
    public boolean active;

    @SerializedName("created_by")
    public String createdBy;

    @SerializedName("created_date")
    public String createdDate;

    @SerializedName("modified_by")
    public String modifiedBy;

    @SerializedName("modified_date")
    public String modifiedDate;

    @SerializedName("async_id")
    public String asyncId;
}