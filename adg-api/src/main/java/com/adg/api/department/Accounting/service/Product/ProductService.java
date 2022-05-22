package com.adg.api.department.Accounting.service.Product;

import com.adg.api.department.Accounting.dal.entity.product.ProductDTO;
import com.adg.api.department.Accounting.dal.entity.product.ProductEntity;
import com.adg.api.department.Accounting.enums.MisaModel;
import com.adg.api.department.Accounting.mapper.ProductMapper;
import com.adg.api.department.Accounting.repository.product.ProductRepository;
import com.adg.api.department.Accounting.service.AbstractMisaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.22 16:33
 */
@Service
public class ProductService extends AbstractMisaService<ProductDTO, ProductEntity, String, ProductMapper, ProductRepository> {

    @Autowired
    public ProductService(ProductMapper mapper, ProductRepository repository) {
        super(mapper, repository);
    }

    @Override
    protected MisaModel getMisaModel() {
        return MisaModel.PRODUCT;
    }
}
