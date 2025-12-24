package com.rehome.main.dto.request;

import lombok.Data;

@Data
public class ChangeCaseStatusRequest {
    private Long statusId;
    private String rejectReason;
}
