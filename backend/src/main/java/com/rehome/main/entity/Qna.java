package com.rehome.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "qna")
@Data
public class Qna {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(name = "question")
	    private String question;

	    @Column(name = "answer")
	    private String answer;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "question_type_id", nullable = false) 
	    private QuestionType questionType;

	
}
