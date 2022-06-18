package com.adg.api.department.Accounting.configuration;

import com.adg.api.department.Accounting.dal.entity.customer.CustomerEntity;
import com.adg.api.department.Accounting.dal.entity.datalake.CustomerOrderProduct.CustomerOrderProductEntity;
import com.adg.api.department.Accounting.dal.entity.employee.EmployeeEntity;
import com.adg.api.department.Accounting.dal.entity.order.OrderEntity;
import com.adg.api.department.Accounting.dal.entity.order_product.OrderProductEntity;
import com.adg.api.department.Accounting.dal.entity.organization_unit.OrganizationUnitEntity;
import com.adg.api.department.Accounting.dal.entity.product.ProductEntity;
import com.adg.api.department.Accounting.dal.entity.stock.StockEntity;
import com.adg.api.department.Accounting.mapper.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.18 14:46
 */
@Configuration
public class MapperConfiguration {

    @Bean
    public StockMapper stockMapper() {
        return new StockMapper(StockEntity.class);
    }

    @Bean
    public ProductMapper productMapper() {
        return new ProductMapper(ProductEntity.class);
    }

    @Bean
    public CustomerMapper customerMapper() {
        return new CustomerMapper(CustomerEntity.class);
    }

    @Bean
    public OrderMapper orderMapper() {
        return new OrderMapper(OrderEntity.class);
    }

    @Bean
    public OrderProductMapper orderProductMapper() {
        return new OrderProductMapper(OrderProductEntity.class);
    }

    @Bean
    public EmployeeMapper employeeMapper() {
        return new EmployeeMapper(EmployeeEntity.class);
    }

    @Bean
    public OrganizationUnitMapper organizationUnitMapper() {
        return new OrganizationUnitMapper(OrganizationUnitEntity.class);
    }

    @Bean
    public CustomerOrderProductMapper customerOrderProductMapper() {
        return new CustomerOrderProductMapper(CustomerOrderProductEntity.class);
    }

}
