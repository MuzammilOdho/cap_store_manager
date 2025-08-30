package com.noor.store.api;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(String code, String message, Map<String, Object> details, Instant timestamp) {
    public static ErrorResponse of(String code, String message, Map<String, Object> details) {
        return new ErrorResponse(code, message, details, Instant.now());
    }
}
