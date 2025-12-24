package com.rehome.main.dto.request;

import lombok.Data;

@Data
public class LostNotificationFormDTO {
    private String caseNumber;
    private String message;
}
