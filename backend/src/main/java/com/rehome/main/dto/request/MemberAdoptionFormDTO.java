package com.rehome.main.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class MemberAdoptionFormDTO {
    private String caseNumber;
    private String maritalStatus;       // 'single', 'married'
    private String employmentStatus;    // 'student', 'employed', 'unemployed'
    private List<Question> questions;     // index:value

    @Data
    public static class Question {
        private Integer questionId;
        private String answer;
    }
}
