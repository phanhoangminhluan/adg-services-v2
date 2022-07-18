package com.adg.api.department.InternationalPayment.inventory.service;

import com.adg.api.department.InternationalPayment.inventory.dto.BankDTO;
import com.adg.api.department.InternationalPayment.inventory.entity.Bank;
import com.adg.api.department.InternationalPayment.inventory.repository.BankRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
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

    @Autowired
    private ObjectMapper objectMapper;

    public Bank findById(@NonNull UUID bankId) {
        Optional<Bank> bankOptional = this.repository.findById(bankId);
        if (bankOptional.isEmpty()) throw new EntityNotFoundException(String.format("There is no bank with id '%s'", bankId));
        return bankOptional.get();
    }

    public List<BankDTO> findAll() {
        List<Bank> banks = this.repository.findAll();
        if (banks.isEmpty())
            throw new EntityNotFoundException("There are no banks currently");
        return this.objectMapper.convertValue(banks, new TypeReference<>() {});
    }
    public Bank findByOtherName(@NonNull String name) {
        Bank bank = this.repository.findByOtherName(name);
        if (Objects.isNull(bank)) throw new EntityNotFoundException(String.format("There is no bank named '%s'", name));
        return bank;
    }

}
