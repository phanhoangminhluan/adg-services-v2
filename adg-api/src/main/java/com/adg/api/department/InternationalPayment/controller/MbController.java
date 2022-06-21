package com.adg.api.department.InternationalPayment.controller;

import com.adg.api.department.InternationalPayment.bank.mb.MbService;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.06.19 22:48
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/international-payment/disbursement/mb/")
@Log4j2
public class MbController {

    @Autowired
    private MbService mbService;

    @PostMapping("import")
    @SneakyThrows
    public Map<String, Object> importFile(@RequestParam("file") MultipartFile file) {
        long t1 = System.currentTimeMillis();

        log.info("Import request. Original File Name: {}. Size: {}. Content Type: {}", file.getOriginalFilename(), file.getSize(), file.getContentType());

        Pair<Map<String, Object>, Map<String, Object>> pair = this.mbService.parseFile(file.getInputStream());

        Map<String, Object> payload = MapUtils.ImmutableMap().put("data", pair.getFirst()).put("status", "ok").build();

        log.info(String.format("Import response. %s", JsonUtils.toJson(payload)));

        this.mbService.sendParseFileNotification(payload, t1, file, pair.getSecond());

        return payload;
    }

    @PostMapping(value = "export",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public byte[] exportFile(@RequestBody Map<String, Object> request) {
        log.info("Export Request: {}", JsonUtils.toJson(request));
        long t1 = System.currentTimeMillis();

        Pair<byte[], Map<String, Object>> pair = this.mbService.generateDisbursementFiles(request);

        this.mbService.sendGenerateDisbursementFilesNotification(request, t1, pair.getSecond());

        return pair.getFirst();
    }

}
