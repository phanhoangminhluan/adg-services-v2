package com.adg.api.department.InternationalPayment.inventory.service;

import com.adg.api.department.InternationalPayment.inventory.entity.Storage;
import com.adg.api.department.InternationalPayment.inventory.repository.StorageRepository;
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
public class StorageService {

    @Autowired
    private StorageRepository repository;

    public Storage findByName(@NonNull String name) {
        Storage storage = this.repository.findByName(name);
        if (Objects.isNull(storage)) throw new EntityNotFoundException(String.format("There is no storage named '%s'", name));
        return storage;
    }

    public Storage findById(@NonNull UUID portId) {
        Optional<Storage> storageOptional = this.repository.findById(portId);
        if (storageOptional.isEmpty()) throw new EntityNotFoundException(String.format("There is no storage id '%s'", portId));
        return storageOptional.get();
    }
}
