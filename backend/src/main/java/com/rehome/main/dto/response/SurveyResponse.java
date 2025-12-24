package com.rehome.main.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 問卷相關回應 DTO
 */
public class SurveyResponse {
    
    /**
     * 單一問卷題目回應
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionItem {
        private Integer id;
        private String question;
        private String content;
        private Integer sortOrder;
    }
    
    /**
     * 問卷題目列表回應
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionList {
        private String type; // "adoption" 或 "surrender"
        private List<QuestionItem> questions;
        private Integer totalCount;
        private LocalDateTime lastUpdated; // 最近更新時間
    }
}
