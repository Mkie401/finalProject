package com.rehome.main.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberCenterFavoritesListResponseDTO {
    private Boolean isRemove;
    private LocalDateTime favoritesDate;

    private CaseCardResponseDTO card;
}
