package com.adg.api.department.Accounting.mapper;

import com.adg.api.department.Accounting.dal.entity.organization_unit.OrganizationUnitEntity;
import com.merlin.mapper.MerlinMapper;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.01 23:51
 */
public class OrganizationUnitMapper extends MerlinMapper<OrganizationUnitEntity> {
    public OrganizationUnitMapper(Class<OrganizationUnitEntity> entityClass) {
        super(entityClass);
    }
}
