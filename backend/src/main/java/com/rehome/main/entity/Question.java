package com.rehome.main.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 問卷題目實體
 */
@Entity
@Table(name = "question")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "question", nullable = false, length = 200)
    private String question;
    
    @Column(name = "content", length = 500)
    private String content;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "question_category", columnDefinition = "ENUM('adoption', 'surrender')")
    private QuestionCategory questionCategory;
    
    @Column(name = "update_at")
    private LocalDateTime updatedAt;
    
    /**
     * 問卷分類枚舉
     */
    public enum QuestionCategory {
        adoption,   // 領養問卷
        surrender   // 送養問卷
    }
}
