package com.adg.api.department.Accounting.service;

import com.adg.api.department.Accounting.enums.MisaModel;
import com.adg.api.department.Accounting.model.MisaSyncDTO;
import com.adg.api.department.Accounting.service.Customer.CustomerService;
import com.adg.api.department.Accounting.service.Employee.EmployeeService;
import com.adg.api.department.Accounting.service.Order.OrderService;
import com.adg.api.department.Accounting.service.OrganizationUnit.OrganizationUnitService;
import com.adg.api.department.Accounting.service.Product.ProductService;
import com.adg.api.department.Accounting.service.Stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.22 00:07
 */
@Component
public class MisaService {

    @Autowired
    private StockService stockService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrganizationUnitService organizationUnitService;

    @Autowired
    private OrderService orderService;

    public void sync(MisaSyncDTO misaSyncDTO) {
        if (misaSyncDTO.getModels().contains(MisaModel.STOCK)) {
            this.stockService.sync(misaSyncDTO);
        }

        if (misaSyncDTO.getModels().contains(MisaModel.EMPLOYEE)) {
            this.employeeService.sync(misaSyncDTO);
        }

        if (misaSyncDTO.getModels().contains(MisaModel.CUSTOMER)) {
            this.customerService.sync(misaSyncDTO);
        }

        if (misaSyncDTO.getModels().contains(MisaModel.PRODUCT)) {
            this.productService.sync(misaSyncDTO);
        }

        if (misaSyncDTO.getModels().contains(MisaModel.ORGANIZATION_UNIT)) {
            this.organizationUnitService.sync(misaSyncDTO);
        }

        if (misaSyncDTO.getModels().contains(MisaModel.ORDER)) {
            this.orderService.sync(misaSyncDTO);
        }

    }

}
