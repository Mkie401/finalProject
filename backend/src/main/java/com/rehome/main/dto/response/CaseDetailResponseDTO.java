package com.rehome.main.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaseDetailResponseDTO {
    private LostDetail lostDetail;
    private AdoptionDetail adoptionDetail;
    private ShelterDetail shelterDetail;

    // 共用描述
    private String description;

    @Data
    @Builder
    public static class LostDetail {
        // 走失協尋用
        private LocalDateTime lostDate;
        private String lostRegion;
        private String lostAddr;
        private BigDecimal lng;
        private BigDecimal lat;
        private String lostProcess;
    }

    @Data
    @Builder
    public static class AdoptionDetail {
        // 送養使用
        private String medicalInfo;
        private String adoptionRequ;
        private Boolean isFollowAger;
        private Boolean isFamilyAger;
        private Boolean isAgeLimit;
        private List<String> cityList;
    }

    @Data
    @Builder
    public static class ShelterDetail {
        // 收容所用
        private LocalDateTime entryDates;
        private Long entryDays; // 計算
        private String foundPlace;
    }
}
