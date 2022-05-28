package com.adg.api.department.InternationalPayment.controller;

import com.adg.api.department.InternationalPayment.service.viettin.ViettinService;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.28 23:01
 */
@RestController
@RequestMapping("/international-payment/disbursement/viettin/")
@Log4j2
@CrossOrigin(origins = "*")
public class ViettinController {

    @Autowired
    private ViettinService viettinService;

    @PostMapping("import")
    @SneakyThrows
    public String importFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> data = viettinService.readInputFile(file.getInputStream());
        return JsonUtils.toJson(MapUtils.ImmutableMap()
                        .put("data", data)
                        .put("status", "ok")
                .build());
    }

}
