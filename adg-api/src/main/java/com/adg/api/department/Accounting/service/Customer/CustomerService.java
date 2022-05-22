package com.adg.api.department.Accounting.service.Customer;

import com.adg.api.department.Accounting.dal.entity.customer.CustomerDTO;
import com.adg.api.department.Accounting.dal.entity.customer.CustomerEntity;
import com.adg.api.department.Accounting.enums.MisaModel;
import com.adg.api.department.Accounting.mapper.CustomerMapper;
import com.adg.api.department.Accounting.repository.customer.CustomerRepository;
import com.adg.api.department.Accounting.service.AbstractMisaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.22 16:15
 */
@Service
public class CustomerService extends AbstractMisaService<CustomerDTO, CustomerEntity, String, CustomerMapper, CustomerRepository> {

    @Autowired
    public CustomerService(CustomerMapper mapper, CustomerRepository repository) {
        super(mapper, repository);
    }

    @Override
    protected MisaModel getMisaModel() {
        return MisaModel.CUSTOMER;
    }
}
