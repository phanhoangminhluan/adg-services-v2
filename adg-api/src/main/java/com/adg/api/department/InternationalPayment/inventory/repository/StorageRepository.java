package com.adg.api.department.InternationalPayment.inventory.repository;

import com.adg.api.department.InternationalPayment.inventory.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 03:25
 */
@Repository
public interface StorageRepository extends JpaRepository<Storage, UUID> {

    Storage findByName(String name);

    List<Storage> findAllByIsPort(boolean isPort);
}
