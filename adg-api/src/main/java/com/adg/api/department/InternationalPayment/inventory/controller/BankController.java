package com.adg.api.department.InternationalPayment.inventory.controller;

import com.adg.api.department.InternationalPayment.inventory.dto.BankDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.PaginationTransactionHistoryDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.ResponseDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.TransactionHistoryDTO;
import com.adg.api.department.InternationalPayment.inventory.service.BankService;
import com.adg.api.department.InternationalPayment.inventory.service.OrderTransactionService;
import com.adg.api.general.http.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.19 06:07
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/international-payment/tracking/bank")
public class BankController {

    @Autowired
    private OrderTransactionService orderTransactionService;

    @Autowired
    private BankService bankService;

    @Autowired
    private ResponseWrapper responseWrapper;


    @GetMapping
    public ResponseEntity<ResponseDTO<List<BankDTO>>> getBanks() {
        return this.responseWrapper.ok(this.bankService.findAll());
    }

    @GetMapping("{bankId}/transaction")
    public ResponseEntity<ResponseDTO<PaginationTransactionHistoryDTO>> getTransactionsByBank(@PathVariable String bankId,  @RequestParam("pageIndex") int pageIndex, @RequestParam("pageSize") int pageSize) {
        List<TransactionHistoryDTO> transactions = List.of(
                TransactionHistoryDTO.builder()
                        .id(UUID.randomUUID())
                        .orderId(UUID.fromString("5d07af9b-0981-43ee-87b7-ca6e0fcb9451"))
                        .sourceStorageId(UUID.fromString("8c89b282-dbde-40eb-98af-c8ba55cd1757"))
                        .targetStorageId(UUID.fromString("e8e8f631-44f4-4412-882e-633f840a2cad"))
                        .releaseDate(ZonedDateTime.now())
                        .releaseQuantity(100)
                        .parentId(UUID.randomUUID())
                        .bankId(UUID.randomUUID())
                        .note("Nothing to note")
                        .build(),
                TransactionHistoryDTO.builder()
                        .id(UUID.randomUUID())
                        .orderId(UUID.randomUUID())
                        .sourceStorageId(UUID.randomUUID())
                        .targetStorageId(UUID.randomUUID())
                        .releaseDate(ZonedDateTime.now())
                        .parentId(UUID.randomUUID())
                        .bankId(UUID.randomUUID())
                        .releaseQuantity(200)
                        .note("Nothing to note")
                        .build(),
                TransactionHistoryDTO.builder()
                        .id(UUID.randomUUID())
                        .orderId(UUID.randomUUID())
                        .sourceStorageId(UUID.randomUUID())
                        .targetStorageId(UUID.randomUUID())
                        .releaseDate(ZonedDateTime.now())
                        .releaseQuantity(300)
                        .parentId(UUID.randomUUID())
                        .bankId(UUID.randomUUID())
                        .note("Nothing to note")
                        .build()
        );
        return this.responseWrapper.ok(PaginationTransactionHistoryDTO.builder()
                        .transactions(transactions)
                        .totalPages(1)
                        .totalRecords(3)
                .build());
    }



}
