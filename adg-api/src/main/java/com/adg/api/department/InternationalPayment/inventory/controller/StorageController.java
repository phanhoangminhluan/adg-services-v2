package com.adg.api.department.InternationalPayment.inventory.controller;

import com.adg.api.department.InternationalPayment.inventory.dto.ResponseDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.StorageDTO;
import com.adg.api.department.InternationalPayment.inventory.service.StorageService;
import com.adg.api.general.http.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.19 06:08
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/international-payment/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private ResponseWrapper responseWrapper;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<StorageDTO>>> getStorages() {
        return this.responseWrapper.ok(this.storageService.findStorages());
    }

}
