package com.adg.api.department.Accounting.repository.employee;

import com.adg.api.department.Accounting.dal.entity.employee.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.04.01 22:50
 */
@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {

}
