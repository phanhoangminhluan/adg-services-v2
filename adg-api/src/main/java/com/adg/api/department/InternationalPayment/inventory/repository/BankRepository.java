package com.adg.api.department.InternationalPayment.inventory.repository;

import com.adg.api.department.InternationalPayment.inventory.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 03:24
 */
@Repository
public interface BankRepository extends JpaRepository<Bank, UUID> {

    Bank findByOtherName(String name);

}
