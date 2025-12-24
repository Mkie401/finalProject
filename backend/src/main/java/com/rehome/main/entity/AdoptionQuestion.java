package com.rehome.main.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
@Table(name = "adoption_question")
@Data
public class AdoptionQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK
    // private Long adoptionMemberId;
    // private Long questionId;

    private String answer;

    
    // ------------------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adoption_member_id", referencedColumnName = "id")
	@JsonBackReference
    private AdoptionMember adoptionMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
	@JsonBackReference
    private Question question;
}
