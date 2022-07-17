package com.adg.api.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.18 00:28
 */
public class BindingResultUtils {
    public static List<String> getErrorMessages(BindingResult bindingResult) {
        return bindingResult.getAllErrors()
                .stream()
                .map(error -> {
                    var defaultMessage = error.getDefaultMessage();
                    if (error instanceof FieldError) {
                        var fieldError = (FieldError) error;
                        return String.format("%s %s", fieldError.getField(), defaultMessage);
                    } else {
                        return defaultMessage;
                    }
                })
                .collect(Collectors.toList());
    }
}
