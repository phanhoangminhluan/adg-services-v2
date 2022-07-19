package com.adg.api.department.InternationalPayment.inventory.dto;

import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.17 12:39
 */
@Builder
@Data
public class ResponseDTO<T> {

    private T data;
    private String duration;
    private String requestAt;
    private String responseAt;
    private List<String> messages;

    private static final String OK_MSG = "success";
    private static final String ERROR_MSG = "fail";

    public static <T> ResponseDTO<T> newOkInstance(T data) {
        return ResponseDTO.<T>builder()
                .data(data)
                .messages(List.of(OK_MSG))
                .build();
    }

    public static <T> ResponseDTO<T> newOkInstance(T data, String message) {
        return ResponseDTO.<T>builder()
                .data(data)
                .messages(List.of(message))
                .build();
    }

    public static <T> ResponseDTO<T> newOkInstance(T data, List<String> messages) {
        return ResponseDTO.<T>builder()
                .data(data)
                .messages(messages)
                .build();
    }

    public static ResponseDTO newErrorInstance(String message) {
        return ResponseDTO.builder()
                .messages(List.of(message))
                .build();
    }

    public static <T> ResponseDTO<T> newErrorInstance(T data) {
        return ResponseDTO.<T>builder()
                .data(data)
                .messages(List.of(ERROR_MSG))
                .build();
    }

    public static <T> ResponseDTO<T> newErrorInstance(@Nullable T data, List<String> errorMessages) {
        return ResponseDTO.<T>builder()
                .data(data)
                .messages(errorMessages)
                .build();
    }

    public static <T> ResponseDTO<T> newErrorInstance(@Nullable T data, String errorMessage) {
        return ResponseDTO.<T>builder()
                .data(data)
                .messages(List.of(errorMessage))
                .build();
    }
}
