package com.adg.api.department.Accounting.service.OrganizationUnit;

import com.adg.api.department.Accounting.dal.entity.organization_unit.OrganizationUnitDTO;
import com.adg.api.department.Accounting.dal.entity.organization_unit.OrganizationUnitEntity;
import com.adg.api.department.Accounting.enums.MisaModel;
import com.adg.api.department.Accounting.mapper.OrganizationUnitMapper;
import com.adg.api.department.Accounting.repository.organization_unit.OrganizationUnitRepository;
import com.adg.api.department.Accounting.service.AbstractMisaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.22 16:37
 */
@Service
public class OrganizationUnitService extends AbstractMisaService<OrganizationUnitDTO, OrganizationUnitEntity, Integer, OrganizationUnitMapper, OrganizationUnitRepository> {

    @Autowired
    public OrganizationUnitService(OrganizationUnitMapper mapper, OrganizationUnitRepository repository) {
        super(mapper, repository);
    }

    @Override
    protected MisaModel getMisaModel() {
        return MisaModel.ORGANIZATION_UNIT;
    }
}
