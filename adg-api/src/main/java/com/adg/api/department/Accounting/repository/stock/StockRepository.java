package com.adg.api.department.Accounting.repository.stock;

import com.adg.api.department.Accounting.dal.entity.stock.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.18 20:33
 */
@Repository
public interface StockRepository extends JpaRepository<StockEntity, String> {
}
