package com.adg.api.department.Accounting.repository.product;

import com.adg.api.department.Accounting.dal.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.18 22:10
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {
}
