package com.rehome.main.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "customer_service_form")
@Data
public class CustomerServiceForm {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_type_id", nullable = false) 
    private QuestionType questionType;

    @Column(name = "question_title", nullable = false)
    private String questionTitle;

    @Column(name = "question_info", nullable = false, length = 1000)
    private String questionInfo;

    @Column(name = "cname", nullable = false)
    private String cname;

    @Column(name = "cmail", nullable = false)
    private String cmail;

    @Column(name = "form_date")
    private Date createdTime;

    @Column(name = "status")
    private String status; // 	enum('unprocessed', 'processed')

    @Column(name = "reply")
    private String reply;

    @Column(name = "reply_date")
    private Date replyDate;

    // 在物件建立前自動填入時間與預設狀態
    @PrePersist
    protected void onCreate() {
        if (createdTime == null) {
            createdTime = new Date();
        }
        if (status == null) {
            status = "unprocessed"; // 預設為未處理
        }
    }

}

