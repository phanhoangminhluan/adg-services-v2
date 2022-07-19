package com.adg.api.department.InternationalPayment.inventory.controller;

import com.adg.api.department.InternationalPayment.inventory.dto.*;
import com.adg.api.department.InternationalPayment.inventory.dto.inventory.GetOrderByPortDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.marker_validator.InsertPurchaseOrderValidator;
import com.adg.api.department.InternationalPayment.inventory.service.CrmOrderService;
import com.adg.api.department.InternationalPayment.inventory.service.reader.DonMuaHangService;
import com.adg.api.general.http.ResponseWrapper;
import com.adg.api.util.BindingResultUtils;
import com.merlin.asset.core.utils.JsonUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.04 06:54
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/international-payment/tracking/order")
@Log4j2
public class OrderTrackingController {

    @Autowired
    private DonMuaHangService donMuaHangService;

    @Autowired
    private CrmOrderService crmOrderService;

    @Autowired
    private ResponseWrapper responseWrapper;

    @PostMapping("read-file")
    @SneakyThrows
    public ResponseEntity readPurchaseOrderFile(@RequestParam("file") MultipartFile file) {

        long t1 = System.currentTimeMillis();

        log.info("Import request. Original File Name: {}. Size: {}. Content Type: {}", file.getOriginalFilename(), file.getSize(), file.getContentType());

        Pair<List<PurchaseOrderDTO>, Map<String, Object>> pair = this.donMuaHangService.parseFile(file.getInputStream());


        FilePurchaseOrderDTO filePurchaseOrderDTO = FilePurchaseOrderDTO.builder()
                .purchaseOrders(pair.getFirst())
                .build();

        log.info(String.format("Import response. %s", JsonUtils.toJson(filePurchaseOrderDTO)));

        this.donMuaHangService.sendParseFileNotification(filePurchaseOrderDTO, t1, file, pair.getSecond());

        return responseWrapper.ok(filePurchaseOrderDTO);

    }

    @PostMapping
    @SneakyThrows
    private ResponseEntity insertOrders(@RequestBody @Validated(InsertPurchaseOrderValidator.class) FilePurchaseOrderDTO filePurchaseOrderDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return this.responseWrapper.error(BindingResultUtils.getErrorMessages(bindingResult));
        }

        return this.crmOrderService.insertDonMuaHangRecord(filePurchaseOrderDTO);
    }

    @GetMapping
    @SneakyThrows
    private GetOrderByPortDTO getOrdersByPort(@RequestParam("portId") String portId, @RequestParam("pageIndex") int pageIndex, @RequestParam("pageSize") int pageSize) {
        return this.crmOrderService.getOrderByPort(portId, pageIndex, pageSize);
    }

    @PutMapping("update-note")
    public ResponseEntity<ResponseDTO<Set<UUID>>> updateNote(@RequestBody @Valid OrderNoteRequestDTO orderNoteRequestDTO) {
        return this.crmOrderService.updateNote(orderNoteRequestDTO.getOrders());
    }

    @GetMapping("{orderId}/transaction")
    public ResponseEntity<ResponseDTO<List<LayeredTransactionHistoryDTO>>> getTransactionByOrderId(@PathVariable String orderId) {

        List<LayeredTransactionHistoryDTO> transactionHistoryDTOs = List.of(
                LayeredTransactionHistoryDTO.builder()
                        .id(UUID.randomUUID())
                        .orderId(UUID.randomUUID())
                        .sourceStorageId(UUID.randomUUID())
                        .targetStorageId(UUID.randomUUID())
                        .releaseDate(ZonedDateTime.now())
                        .releaseQuantity(200)
                        .bankId(UUID.randomUUID())
                        .parentId(UUID.randomUUID())
                        .children(List.of(
                                LayeredTransactionHistoryDTO.builder()
                                        .id(UUID.randomUUID())
                                        .orderId(UUID.randomUUID())
                                        .sourceStorageId(UUID.randomUUID())
                                        .targetStorageId(UUID.randomUUID())
                                        .bankId(UUID.randomUUID())
                                        .parentId(UUID.randomUUID())
                                        .releaseDate(ZonedDateTime.now())
                                        .releaseQuantity(150).build(),
                                LayeredTransactionHistoryDTO.builder()
                                        .id(UUID.randomUUID())
                                        .orderId(UUID.randomUUID())
                                        .sourceStorageId(UUID.randomUUID())
                                        .targetStorageId(UUID.randomUUID())
                                        .bankId(UUID.randomUUID())
                                        .parentId(UUID.randomUUID())
                                        .releaseDate(ZonedDateTime.now())
                                        .releaseQuantity(50).build()
                        ))
                        .note("Nothing to note")
                        .build(),
                LayeredTransactionHistoryDTO.builder()
                        .id(UUID.randomUUID())
                        .orderId(UUID.randomUUID())
                        .sourceStorageId(UUID.randomUUID())
                        .targetStorageId(UUID.randomUUID())
                        .releaseDate(ZonedDateTime.now())
                        .bankId(UUID.randomUUID())
                        .parentId(UUID.randomUUID())
                        .releaseQuantity(200)
                        .children(List.of(
                                LayeredTransactionHistoryDTO.builder()
                                        .id(UUID.randomUUID())
                                        .orderId(UUID.randomUUID())
                                        .sourceStorageId(UUID.randomUUID())
                                        .bankId(UUID.randomUUID())
                                        .parentId(UUID.randomUUID())
                                        .targetStorageId(UUID.randomUUID())
                                        .releaseDate(ZonedDateTime.now())
                                        .releaseQuantity(150).build(),
                                LayeredTransactionHistoryDTO.builder()
                                        .id(UUID.randomUUID())
                                        .orderId(UUID.randomUUID())
                                        .sourceStorageId(UUID.randomUUID())
                                        .bankId(UUID.randomUUID())
                                        .parentId(UUID.randomUUID())
                                        .targetStorageId(UUID.randomUUID())
                                        .releaseDate(ZonedDateTime.now())
                                        .releaseQuantity(50).build()
                        ))
                        .note("Nothing to note")
                        .build(),
                LayeredTransactionHistoryDTO.builder()
                        .id(UUID.randomUUID())
                        .bankId(UUID.randomUUID())
                        .parentId(UUID.randomUUID())
                        .orderId(UUID.randomUUID())
                        .sourceStorageId(UUID.randomUUID())
                        .targetStorageId(UUID.randomUUID())
                        .releaseDate(ZonedDateTime.now())
                        .releaseQuantity(200)
                        .children(List.of(
                                LayeredTransactionHistoryDTO.builder()
                                        .id(UUID.randomUUID())
                                        .orderId(UUID.randomUUID())
                                        .sourceStorageId(UUID.randomUUID())
                                        .targetStorageId(UUID.randomUUID())
                                        .bankId(UUID.randomUUID())
                                        .parentId(UUID.randomUUID())
                                        .releaseDate(ZonedDateTime.now())
                                        .releaseQuantity(150).build(),
                                LayeredTransactionHistoryDTO.builder()
                                        .id(UUID.randomUUID())
                                        .orderId(UUID.randomUUID())
                                        .sourceStorageId(UUID.randomUUID())
                                        .targetStorageId(UUID.randomUUID())
                                        .bankId(UUID.randomUUID())
                                        .parentId(UUID.randomUUID())
                                        .releaseDate(ZonedDateTime.now())
                                        .releaseQuantity(50).build()
                        ))
                        .note("Nothing to note")
                        .build()
        );

        return this.responseWrapper.ok(transactionHistoryDTOs);
    }

}
