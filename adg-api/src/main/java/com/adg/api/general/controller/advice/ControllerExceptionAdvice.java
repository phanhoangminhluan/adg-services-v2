package com.adg.api.general.controller.advice;

import com.adg.api.department.InternationalPayment.inventory.dto.ResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.17 17:26
 */
@ControllerAdvice
@Log4j2
public class ControllerExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { Exception.class})
    protected ResponseEntity<Object> handleGeneralException(
            RuntimeException ex, WebRequest request) {
        this.logErrorInfo(ex, request);
        return handleExceptionInternal(ex, ResponseDTO.newErrorInstance(ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }


    private void logErrorInfo(Exception ex, WebRequest request) {
        try {
            ContentCachingRequestWrapper nativeRequest = (ContentCachingRequestWrapper) ((ServletWebRequest) request).getNativeRequest();
            String requestEntityAsString = new String(nativeRequest.getContentAsByteArray());

            log.error("API throws exception. Request endpoint: {}. Request body: {}", nativeRequest.getRequestURI(), requestEntityAsString, ex);
        } finally {
            log.error(ex);
        }
    }
}
