package com.adg.api.department.InternationalPayment.inventory.service;

import com.adg.api.department.InternationalPayment.inventory.dto.StorageDTO;
import com.adg.api.department.InternationalPayment.inventory.entity.Storage;
import com.adg.api.department.InternationalPayment.inventory.repository.StorageRepository;
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
public class StorageService {

    @Autowired
    private StorageRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    public Storage findByName(@NonNull String name) {
        Storage storage = this.repository.findByName(name);
        if (Objects.isNull(storage)) throw new EntityNotFoundException(String.format("There is no storage named '%s'", name));
        return storage;
    }

    public List<StorageDTO> findStorages() {
        List<Storage> storages = this.repository.findAllByIsPort(false);
        return this.objectMapper.convertValue(storages, new TypeReference<>() {});
    }

    public Storage findById(@NonNull UUID portId) {
        Optional<Storage> storageOptional = this.repository.findById(portId);
        if (storageOptional.isEmpty()) throw new EntityNotFoundException(String.format("There is no storage id '%s'", portId));
        return storageOptional.get();
    }
}
