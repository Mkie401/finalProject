package com.rehome.main.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 問卷相關請求 DTO
 */
public class SurveyRequest {
    
    /**
     * 儲存問卷題目列表請求
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveQuestions {
        
        @NotBlank(message = "問卷類型不可為空")
        private String type; // "adoption" 或 "surrender"
        
        @NotNull(message = "題目列表不可為空")
        @Valid
        private List<QuestionItem> questions;
    }
    
    /**
     * 單一問卷題目
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionItem {
        
        private Integer id; // 新增題目時為 null，編輯時保留原 id
        
        @NotBlank(message = "題目不可為空")
        private String question;
        
        private String content; // 範例/說明，可為空
        
        @NotNull(message = "排序不可為空")
        private Integer sortOrder;
    }
}
