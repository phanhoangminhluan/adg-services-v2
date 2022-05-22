package com.adg.api.department.Accounting.controller;

import com.adg.api.department.Accounting.model.MisaSyncDTO;
import com.adg.api.department.Accounting.service.MisaService;
import com.merlin.asset.core.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.21 21:58
 */
@RestController
@RequestMapping("accounting")
public class MisaController {

    @Autowired
    private MisaService misaService;

    @PostMapping("sync-misa")
    public Map<String, Object> syncMisa(@RequestBody MisaSyncDTO body) {
        this.misaService.sync(body);
        return MapUtils.ImmutableMap().build();
    }

}
