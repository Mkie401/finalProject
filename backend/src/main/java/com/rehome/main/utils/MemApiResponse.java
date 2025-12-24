package com.rehome.main.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> MemApiResponse<T> success(T data) {
        return new MemApiResponse<T>(true, "成功", data);
    }

    public static <T> MemApiResponse<T> failure(String message) {
        return new MemApiResponse<T>(false, message, null);
    }
}