package com.adg.api.general.http;

import com.merlin.asset.core.utils.DateTimeUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.17 14:42
 */
@Component
public class HttpStatsInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String currentTime = DateTimeUtils.convertZonedDateTimeToFormat(ZonedDateTime.now(), "Asia/Ho_Chi_Minh", DateTimeUtils.FMT_01);
        response.addHeader("request_at", currentTime);
        response.addHeader("request_at_ms", System.currentTimeMillis() + "");
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
