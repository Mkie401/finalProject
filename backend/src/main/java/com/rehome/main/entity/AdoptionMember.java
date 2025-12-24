package com.rehome.main.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "adoption_member")
@Data
public class AdoptionMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK
    // private Long adoptionStatusId;
    // private Long caseId;
    // private Long memberId;


    
    @Column(name = "employment_status")
    private String employmentStatus;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(updatable = false, insertable = false)
    private LocalDateTime createdAt;
    private LocalDateTime endAt;

    
    // ------------------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adoption_status_id")
    @JsonBackReference
    private AdoptionStatus adoptionStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", referencedColumnName = "id")
    @JsonBackReference
    private Case petCase; // 因為 case 是保留字，所以改成醬

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;

    @OneToMany(mappedBy = "adoptionMember", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdoptionQuestion> adoptionQuestions;
}
