package com.rehome.main.dto.response;

import lombok.Data;

@Data
public class QnaResponse {
	private Long id;
    private String question;
    private String answer;
    private String questionType; 
    
    
    public QnaResponse(Long id, String question, String answer, String questionType) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.questionType = questionType;
    }
    
}
