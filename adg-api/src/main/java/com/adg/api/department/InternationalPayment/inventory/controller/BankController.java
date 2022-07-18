package com.adg.api.department.InternationalPayment.inventory.controller;

import com.adg.api.department.InternationalPayment.inventory.dto.BankDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.ResponseDTO;
import com.adg.api.department.InternationalPayment.inventory.service.BankService;
import com.adg.api.general.http.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.19 06:07
 */
@RestController
@RequestMapping("/international-payment/bank")
public class BankController {


    @Autowired
    private BankService bankService;

    @Autowired
    private ResponseWrapper responseWrapper;


    @GetMapping
    public ResponseEntity<ResponseDTO<List<BankDTO>>> getBanks() {
        return this.responseWrapper.ok(this.bankService.findAll());
    }

}
