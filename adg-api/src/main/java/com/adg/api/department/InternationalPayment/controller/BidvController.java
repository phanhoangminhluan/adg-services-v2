package com.adg.api.department.InternationalPayment.controller;

import com.adg.api.department.InternationalPayment.service.bidv.BidvService;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.12 02:25
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/international-payment/disbursement/bidv/")
@Log4j2
public class BidvController {

    @Autowired
    private BidvService bidvService;

    @Value("${international-payment.bidv.input.zip}")
    private String inputZip;

    @GetMapping
    public Map<String, Object> get() {
        return MapUtils.ImmutableMap()
                .put("data", MapUtils.ImmutableMap().build())
                .put("status", "ok")
                .build();
    }

    @PostMapping("import")
    @SneakyThrows
    public Map<String, Object> importFile(@RequestParam("file") MultipartFile file) {
        log.info("Import request. Original File Name: {}. Size: {}. Content Type: {}", file.getOriginalFilename(), file.getSize(), file.getContentType());
        Map<String, Object> data = this.bidvService.parseFile(file.getInputStream());
        return MapUtils.ImmutableMap()
                .put("data", data)
                .put("status", "ok")
                .build();
    }

    @PostMapping(value = "export",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public byte[] exportFile(@RequestBody Map<String, Object> request) {
        log.info("Export Request: {}", JsonUtils.toJson(request));
        return this.bidvService.generateDisbursementFiles(request);
    }

}
