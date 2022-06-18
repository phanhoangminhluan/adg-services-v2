package com.adg.api.department.Accounting.service;

import com.adg.api.department.Accounting.enums.MisaModel;
import com.adg.api.department.Accounting.model.MisaSyncDTO;
import com.adg.api.department.Accounting.service.Customer.CustomerService;
import com.adg.api.department.Accounting.service.CustomerOrderProduct.CustomerOrderProductService;
import com.adg.api.department.Accounting.service.Employee.EmployeeService;
import com.adg.api.department.Accounting.service.Order.OrderService;
import com.adg.api.department.Accounting.service.OrganizationUnit.OrganizationUnitService;
import com.adg.api.department.Accounting.service.Product.ProductService;
import com.adg.api.department.Accounting.service.Stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.22 00:07
 */
@Component
public class MisaService {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);

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
    private CustomerOrderProductService customerOrderProductService;

    @Autowired
    private OrderService orderService;

    public void sync(MisaSyncDTO misaSyncDTO) {
        if (misaSyncDTO.getModels().contains(MisaModel.ORDER)) {
            EXECUTOR_SERVICE.execute(() -> {
                this.orderService.sync(misaSyncDTO);
            });
        }

        if (misaSyncDTO.getModels().contains(MisaModel.CUSTOMER)) {
            EXECUTOR_SERVICE.execute(() -> {
                this.customerService.sync(misaSyncDTO);
            });
        }

        if (misaSyncDTO.getModels().contains(MisaModel.PRODUCT)) {
            EXECUTOR_SERVICE.execute(() -> {
                this.productService.sync(misaSyncDTO);
            });
        }

        if (misaSyncDTO.getModels().contains(MisaModel.STOCK)) {
            EXECUTOR_SERVICE.execute(() -> {
                this.stockService.sync(misaSyncDTO);
            });
        }

        if (misaSyncDTO.getModels().contains(MisaModel.EMPLOYEE)) {
            EXECUTOR_SERVICE.execute(() -> {
                this.employeeService.sync(misaSyncDTO);
            });
        }

        if (misaSyncDTO.getModels().contains(MisaModel.ORGANIZATION_UNIT)) {
            EXECUTOR_SERVICE.execute(() -> {
                this.organizationUnitService.sync(misaSyncDTO);
            });
        }

        if (misaSyncDTO.getModels().contains(MisaModel.CUSTOMER_ORDER_PRODUCT)) {
            EXECUTOR_SERVICE.execute(() -> {
                customerOrderProductService.sync(misaSyncDTO);
            });
        }
    }
}
