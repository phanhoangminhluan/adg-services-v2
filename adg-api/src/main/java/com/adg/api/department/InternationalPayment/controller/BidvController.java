package com.adg.api.department.InternationalPayment.controller;

import com.adg.api.department.Accounting.service.SlackService;
import com.adg.api.department.InternationalPayment.service.bidv.BidvService;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
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

    @Autowired
    private SlackService slackService;

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
        long t1 = System.currentTimeMillis();

        log.info("Import request. Original File Name: {}. Size: {}. Content Type: {}", file.getOriginalFilename(), file.getSize(), file.getContentType());

        Pair<Map<String, Object>, Map<String, Object>> pair = this.bidvService.parseFile(file.getInputStream());

        Map<String, Object> payload = MapUtils.ImmutableMap().put("data", pair.getFirst()).put("status", "ok").build();

        log.info(String.format("Import response. %s", JsonUtils.toJson(payload)));

        this.bidvService.sendParseFileNotification(payload, t1, file, pair.getSecond());

        return payload;
    }

    @PostMapping(value = "export",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public byte[] exportFile(@RequestBody Map<String, Object> request) {
        log.info("Export Request: {}", JsonUtils.toJson(request));
        long t1 = System.currentTimeMillis();

        Pair<byte[], Map<String, Object>> pair =  this.bidvService.generateDisbursementFiles(request);
        this.bidvService.sendGenerateDisbursementFilesNotification(request, t1, pair.getSecond());
        return pair.getFirst();
    }

}
