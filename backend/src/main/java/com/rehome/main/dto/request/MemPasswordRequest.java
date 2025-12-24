package com.rehome.main.dto.request;

import lombok.Data;

@Data
public class MemPasswordRequest {
    private String oldPassword;
    private String newPassword;
}
