package com.rehome.main.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rehome.main.dto.request.SurveyRequest;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.SurveyResponse;
import com.rehome.main.entity.Question;
import com.rehome.main.entity.Question.QuestionCategory;
import com.rehome.main.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 問卷服務
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyService {

    private final QuestionRepository questionRepository;

    /**
     * 根據問卷類型獲取題目列表
     * 
     * @param type 問卷類型（adoption 或 surrender）
     * @return 問卷題目列表
     */
    public ApiResponse<SurveyResponse.QuestionList> getQuestions(String type) {
        try {
            // 驗證問卷類型
            QuestionCategory category = validateAndConvertType(type);
            if (category == null) {
                return ApiResponse.fail("無效的問卷類型，僅支援 adoption 或 surrender");
            }

            // 查詢題目列表
            List<Question> questions = questionRepository.findByQuestionCategoryOrderBySortOrderAsc(category);
            
            // 轉換為 Response DTO
            List<SurveyResponse.QuestionItem> questionItems = questions.stream()
                    .map(this::convertToQuestionItem)
                    .collect(Collectors.toList());

            // 獲取最近更新時間
            LocalDateTime lastUpdated = questions.stream()
                    .map(Question::getUpdatedAt)
                    .filter(updatedAt -> updatedAt != null)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            SurveyResponse.QuestionList result = SurveyResponse.QuestionList.builder()
                    .type(type)
                    .questions(questionItems)
                    .totalCount(questionItems.size())
                    .lastUpdated(lastUpdated)
                    .build();

            return ApiResponse.success("取得問卷題目成功", result);

        } catch (Exception e) {
            log.error("取得問卷題目失敗: {}", e.getMessage(), e);
            return ApiResponse.fail("系統錯誤，請稍後再試");
        }
    }

    /**
     * 儲存問卷題目列表
     * 
     * @param request 儲存請求
     * @return 儲存結果
     */
    @Transactional
    public ApiResponse<SurveyResponse.QuestionList> saveQuestions(SurveyRequest.SaveQuestions request) {
        try {
            // 驗證問卷類型
            QuestionCategory category = validateAndConvertType(request.getType());
            if (category == null) {
                return ApiResponse.fail("無效的問卷類型，僅支援 adoption 或 surrender");
            }

            log.info("開始儲存問卷 - 類型: {}, 題目數量: {}", request.getType(), request.getQuestions().size());

            // 先查詢現有題目
            List<Question> existingQuestions = questionRepository.findByQuestionCategoryOrderBySortOrderAsc(category);
            log.info("找到現有題目數量: {}", existingQuestions.size());
            
            List<Question> savedQuestions = new java.util.ArrayList<>();
            
            // 更新或新增題目
            for (int i = 0; i < request.getQuestions().size(); i++) {
                SurveyRequest.QuestionItem item = request.getQuestions().get(i);
                Question question;
                
                if (i < existingQuestions.size()) {
                    // 更新現有題目
                    question = existingQuestions.get(i);
                    question.setQuestion(item.getQuestion());
                    question.setContent(item.getContent());
                    question.setSortOrder(item.getSortOrder());
                    question.setUpdatedAt(LocalDateTime.now()); // 手動設定更新時間
                    log.info("更新題目 ID: {}", question.getId());
                } else {
                    // 新增題目
                    question = convertToEntity(item, category);
                    question.setUpdatedAt(LocalDateTime.now()); // 手動設定更新時間
                    log.info("新增題目: {}", item.getQuestion());
                }
                
                savedQuestions.add(questionRepository.save(question));
            }
            
            // 刪除多餘的題目（如果新題目數量少於舊題目）
            if (existingQuestions.size() > request.getQuestions().size()) {
                List<Question> toDelete = existingQuestions.subList(request.getQuestions().size(), existingQuestions.size());
                log.info("準備刪除 {} 個多餘題目", toDelete.size());
                
                // 先檢查是否有外鍵約束，如果有就不刪除，只標記為不使用
                try {
                    questionRepository.deleteAll(toDelete);
                    log.info("已刪除多餘題目");
                } catch (Exception e) {
                    log.warn("無法刪除題目（可能有外鍵約束）: {}", e.getMessage());
                    // 如果無法刪除，可以考慮加入軟刪除標記或其他處理方式
                }
            }

            log.info("成功儲存 {} 個題目", savedQuestions.size());

            // 重新查詢以獲取最新的 updated_at
            savedQuestions = questionRepository.findByQuestionCategoryOrderBySortOrderAsc(category);

            // 轉換為 Response DTO
            List<SurveyResponse.QuestionItem> questionItems = savedQuestions.stream()
                    .map(this::convertToQuestionItem)
                    .collect(Collectors.toList());

            // 獲取最近更新時間
            LocalDateTime lastUpdated = savedQuestions.stream()
                    .map(Question::getUpdatedAt)
                    .filter(updatedAt -> updatedAt != null)
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());

            SurveyResponse.QuestionList result = SurveyResponse.QuestionList.builder()
                    .type(request.getType())
                    .questions(questionItems)
                    .totalCount(questionItems.size())
                    .lastUpdated(lastUpdated)
                    .build();

            return ApiResponse.success("問卷儲存成功", result);

        } catch (Exception e) {
            log.error("儲存問卷題目失敗 - 類型: {}, 錯誤訊息: {}", 
                    request.getType(), e.getMessage(), e);
            return ApiResponse.fail("系統錯誤：" + e.getMessage());
        }
    }

    /**
     * 驗證並轉換問卷類型
     * 
     * @param type 問卷類型字串
     * @return QuestionCategory 或 null（無效類型）
     */
    private QuestionCategory validateAndConvertType(String type) {
        try {
            return QuestionCategory.valueOf(type.toLowerCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 將 Entity 轉換為 Response DTO
     * 
     * @param question 問卷題目 Entity
     * @return QuestionItem DTO
     */
    private SurveyResponse.QuestionItem convertToQuestionItem(Question question) {
        return SurveyResponse.QuestionItem.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .content(question.getContent())
                .sortOrder(question.getSortOrder())
                .build();
    }

    /**
     * 將 Request DTO 轉換為 Entity
     * 
     * @param item 問卷題目 Request DTO
     * @param category 問卷分類
     * @return Question Entity
     */
    private Question convertToEntity(SurveyRequest.QuestionItem item, QuestionCategory category) {
        return Question.builder()
                .question(item.getQuestion())
                .content(item.getContent())
                .sortOrder(item.getSortOrder())
                .questionCategory(category)
                // 不設定 updatedAt，讓資料庫自動處理
                .build();
    }
}
