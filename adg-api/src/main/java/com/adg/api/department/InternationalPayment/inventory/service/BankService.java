package com.adg.api.department.InternationalPayment.inventory.service;

import com.adg.api.department.InternationalPayment.inventory.entity.Bank;
import com.adg.api.department.InternationalPayment.inventory.repository.BankRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 03:25
 */
@Service
public class BankService {

    @Autowired
    private BankRepository repository;

    public Bank findById(@NonNull UUID bankId) {
        Optional<Bank> bankOptional = this.repository.findById(bankId);
        if (bankOptional.isEmpty()) throw new EntityNotFoundException(String.format("There is no bank with id '%s'", bankId));
        return bankOptional.get();
    }
    public Bank findByOtherName(@NonNull String name) {
        Bank bank = this.repository.findByOtherName(name);
        if (Objects.isNull(bank)) throw new EntityNotFoundException(String.format("There is no bank named '%s'", name));
        return bank;
    }

}
