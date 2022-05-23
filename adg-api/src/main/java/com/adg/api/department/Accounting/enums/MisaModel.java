package com.adg.api.department.Accounting.enums;

import com.adg.api.department.Accounting.dal.entity.customer.CustomerDTO;
import com.adg.api.department.Accounting.dal.entity.employee.EmployeeDTO;
import com.adg.api.department.Accounting.dal.entity.order.OrderDTO;
import com.adg.api.department.Accounting.dal.entity.organization_unit.OrganizationUnitDTO;
import com.adg.api.department.Accounting.dal.entity.product.ProductDTO;
import com.adg.api.department.Accounting.dal.entity.stock.StockDTO;
import com.adg.api.department.Accounting.service.AbstractDTO;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.21 22:03
 */
public enum MisaModel {

    ORDER("SaleOrders", "modified_date",  MisaOrderType.DESC, OrderDTO.class), // 35586
    CUSTOMER("Customers", "modified_date", MisaOrderType.DESC, CustomerDTO.class), // 2662
    PRODUCT("Products", "modified_date",  MisaOrderType.DESC, ProductDTO.class), // 1059
    STOCK("Stocks", "modified_date",  MisaOrderType.DESC, StockDTO.class), // 175
    EMPLOYEE("Employees", "modified_date",  MisaOrderType.DESC, EmployeeDTO.class), // 137
    ORGANIZATION_UNIT("OrganizationUnits", "modified_date",  MisaOrderType.DESC, OrganizationUnitDTO.class); // 26

    public final String uri;
    public final String orderByField;
    public final MisaOrderType orderType;
    public final Class<? extends AbstractDTO> dtoClass;

    MisaModel(String uri, String orderByField, MisaOrderType orderType, Class<? extends AbstractDTO> dtoClass) {
        this.uri = uri;
        this.orderByField = orderByField;
        this.orderType = orderType;
        this.dtoClass = dtoClass;
    }

}
