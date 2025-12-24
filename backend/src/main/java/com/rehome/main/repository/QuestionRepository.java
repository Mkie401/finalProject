package com.rehome.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rehome.main.entity.Question;
import com.rehome.main.entity.Question.QuestionCategory;

/**
 * 問卷題目 Repository
 */
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    
    /**
     * 根據問卷分類查詢題目，並依排序欄位排序
     * 
     * @param category 問卷分類（adoption 或 surrender）
     * @return 問卷題目列表
     */
    @Query("SELECT q FROM Question q WHERE q.questionCategory = :category ORDER BY q.sortOrder ASC")
    List<Question> findByQuestionCategoryOrderBySortOrderAsc(@Param("category") QuestionCategory category);
    
    /**
     * 根據問卷分類查詢題目數量
     * 
     * @param category 問卷分類
     * @return 題目數量
     */
    Long countByQuestionCategory(QuestionCategory category);
}
