package com.adg.api.general.controller;

import com.merlin.asset.core.utils.MapUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.21 21:00
 */
@RestController
public class HomeController {

    @GetMapping
    public Map<String, Object> home() {
        return MapUtils.ImmutableMap()
                .put("data", MapUtils.ImmutableMap().build())
                .put("status", "ok")
                .build();
    }

}
