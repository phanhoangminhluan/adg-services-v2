package com.adg.api.department.Accounting.service.Employee;

import com.adg.api.department.Accounting.dal.entity.employee.EmployeeDTO;
import com.adg.api.department.Accounting.dal.entity.employee.EmployeeEntity;
import com.adg.api.department.Accounting.enums.MisaModel;
import com.adg.api.department.Accounting.mapper.EmployeeMapper;
import com.adg.api.department.Accounting.repository.employee.EmployeeRepository;
import com.adg.api.department.Accounting.service.AbstractMisaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.22 15:59
 */

@Service
public class EmployeeService extends AbstractMisaService<EmployeeDTO, EmployeeEntity, Integer, EmployeeMapper, EmployeeRepository> {

    @Autowired
    public EmployeeService(EmployeeMapper mapper, EmployeeRepository repository) {
        super(mapper, repository);
    }

    @Override
    protected MisaModel getMisaModel() {
        return MisaModel.EMPLOYEE;
    }
}
