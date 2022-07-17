package com.adg.api.general.http;

import com.adg.api.department.InternationalPayment.inventory.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.17 12:42
 */
@Component
public class ResponseWrapper {

    public static Object NULL_DATA = null;
    public <T> ResponseEntity<ResponseDTO<T>> ok(T data) {
        return ResponseEntity.ok(ResponseDTO.newOkInstance(data));
    }

    public <T> ResponseEntity<ResponseDTO<T>> ok(T data, String message) {
        return ResponseEntity.ok(ResponseDTO.newOkInstance(data, message));
    }

    public <T> ResponseEntity<ResponseDTO<T>> ok(T data, List<String> messages) {
        return ResponseEntity.ok(ResponseDTO.newOkInstance(data, messages));
    }

    public <T> ResponseEntity<ResponseDTO<T>> error(T data) {
        return ResponseEntity.badRequest().body(ResponseDTO.newErrorInstance(data));
    }

    public <T> ResponseEntity<ResponseDTO<T>> error(T data, List<String> messageDetails) {
        return ResponseEntity.badRequest().body(ResponseDTO.newErrorInstance(data, messageDetails));
    }

}
