package com.adg.api.general.controller.advice;

import com.adg.api.department.InternationalPayment.inventory.dto.ResponseDTO;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.ParserUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.17 17:09
 */
@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {

        if (body instanceof ResponseDTO) {
            DecimalFormat df = new DecimalFormat("#.##");
            ResponseDTO responseBody = (ResponseDTO) body;
            String currentTime = DateTimeUtils.convertZonedDateTimeToFormat(ZonedDateTime.now(), "Asia/Ho_Chi_Minh", DateTimeUtils.FMT_01);

            List<String> values = response.getHeaders().getOrEmpty("request_at_ms");
            if (!values.isEmpty()) {
                long requestAtMs = ParserUtils.toLong(values.get(0));
                long duration = System.currentTimeMillis() - requestAtMs;
                String durationStr = duration + " milliseconds";
                if (duration >= 1000) {
                    durationStr = df.format(duration / 1000.0) + " seconds";
                }
                if (duration >= (1000 * 60)) {
                    durationStr = df.format(duration / 1000.0 / 60.0) + " minutes";
                }

                responseBody.setDuration(durationStr);
                responseBody.setResponseAt(currentTime);
            }

            values = response.getHeaders().getOrEmpty("request_at");
            if (!values.isEmpty()) {
                responseBody.setRequestAt(values.get(0));
            }

            return responseBody;
        } else {
            return body;
        }
    }
}
