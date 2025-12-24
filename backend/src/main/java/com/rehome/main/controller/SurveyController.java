package com.rehome.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.request.SurveyRequest;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.SurveyResponse;
import com.rehome.main.service.SurveyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 問卷控制器
 */
@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SurveyController {

    private final SurveyService surveyService;

    /**
     * 根據問卷類型獲取題目列表
     * GET /api/survey/{type}
     * 
     * @param type 問卷類型（adoption 或 surrender）
     * @return 問卷題目列表
     */
    @GetMapping("/{type}")
    public ResponseEntity<ApiResponse<SurveyResponse.QuestionList>> getQuestions(
            @PathVariable String type) {
        log.info("取得問卷題目 - 類型: {}", type);
        
        ApiResponse<SurveyResponse.QuestionList> response = surveyService.getQuestions(type);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 儲存問卷題目列表
     * POST /api/survey/save
     * 
     * @param request 儲存請求
     * @param bindingResult 驗證結果
     * @return 儲存結果
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<SurveyResponse.QuestionList>> saveQuestions(
            @Valid @RequestBody SurveyRequest.SaveQuestions request,
            BindingResult bindingResult) {
        try {
            log.info("收到儲存問卷請求 - 類型: {}, 題目數量: {}", 
                    request != null ? request.getType() : "null", 
                    request != null && request.getQuestions() != null ? request.getQuestions().size() : 0);
            
            // 驗證請求參數
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getFieldError() != null 
                        ? bindingResult.getFieldError().getDefaultMessage() 
                        : "請求參數錯誤";
                log.error("參數驗證失敗: {}", errorMessage);
                return ResponseEntity.badRequest()
                        .body(ApiResponse.fail(errorMessage));
            }
            
            ApiResponse<SurveyResponse.QuestionList> response = surveyService.saveQuestions(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error("儲存問卷時發生未預期的錯誤", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.fail("系統錯誤: " + e.getMessage()));
        }
    }
}
