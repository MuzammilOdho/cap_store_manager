package com.noor.store.api;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data, String message) { return new ApiResponse<>(true, message, data); }
    public static <T> ApiResponse<T> failure(String message) { return new ApiResponse<>(false, message, null); }
}
