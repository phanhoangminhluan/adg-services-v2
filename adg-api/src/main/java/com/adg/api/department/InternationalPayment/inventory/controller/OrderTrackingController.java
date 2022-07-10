package com.adg.api.department.InternationalPayment.inventory.controller;

import com.adg.api.department.InternationalPayment.inventory.dto.inventory.GetOrderByPortDTO;
import com.adg.api.department.InternationalPayment.inventory.entity.Order;
import com.adg.api.department.InternationalPayment.inventory.service.CrmOrderService;
import com.adg.api.department.InternationalPayment.inventory.service.reader.DonMuaHangService;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.04 06:54
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/international-payment/tracking/tcb/")
@Log4j2
public class OrderTrackingController {

    @Autowired
    private DonMuaHangService donMuaHangService;

    @Autowired
    private CrmOrderService crmOrderService;

    @PostMapping("order/import")
    @SneakyThrows
    private Map<String, Object> importFile(@RequestParam("file") MultipartFile file) {

        long t1 = System.currentTimeMillis();

        log.info("Import request. Original File Name: {}. Size: {}. Content Type: {}", file.getOriginalFilename(), file.getSize(), file.getContentType());

        Pair<Map<String, Object>, Map<String, Object>> pair = this.donMuaHangService.parseFile(file.getInputStream());

        Map<String, Object> payload = MapUtils.ImmutableMap().put("data", pair.getFirst()).put("status", "ok").build();

        log.info(String.format("Import response. %s", JsonUtils.toJson(payload)));

        this.donMuaHangService.sendParseFileNotification(payload, t1, file, pair.getSecond());

        return payload;

    }

    @PostMapping("order/confirm")
    @SneakyThrows
    private List<Order> confirmOrder(@RequestBody Map<String, Object> request) {

        return this.crmOrderService.insertDonMuaHangRecord(request);
    }

    @GetMapping("inventory")
    @SneakyThrows
    private GetOrderByPortDTO getOrdersByPort(@RequestParam("port") String port, @RequestParam("pageIndex") int pageIndex, @RequestParam("pageSize") int pageSize) {
        return this.crmOrderService.getOrderByPort(port, pageIndex, pageSize);
    }

}
