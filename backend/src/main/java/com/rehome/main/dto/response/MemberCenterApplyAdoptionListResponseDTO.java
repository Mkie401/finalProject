package com.rehome.main.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberCenterApplyAdoptionListResponseDTO {
    private Boolean isRemove;
    private LocalDateTime createdAt;
    private LocalDateTime endAt;

    private CaseCardResponseDTO card;
}
