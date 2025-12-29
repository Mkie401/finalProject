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
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cases")
@Data
public class Case {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_number")
    private String caseNumber;

    // FK
    // private Long caseTypeId;
    // private Long caseStatusId;
    // @Column(name = "member_id")
    // private Long memberId;
    @Column(name = "case_date_start")
    private LocalDateTime caseDateStart;
    @Column(name = "case_date_end")
    private LocalDateTime caseDateEnd;
    private String description;

    // ------------------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_type_id")
    @JsonBackReference
    private CaseType caseType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_status_id")
    @JsonBackReference
    private CaseStatus caseStatus;

    @OneToOne(mappedBy = "petCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PetInfo petInfo;

    @OneToOne(mappedBy = "petCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PetDetail petDetail;

    @OneToOne(mappedBy = "petCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Contact contact;

    @OneToMany(mappedBy = "petCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "sort_order")
    private List<PetImage> petImage;

    @OneToMany(mappedBy = "petCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "id")
    private List<AdoptionMember> adoptionMembers;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;

    @OneToMany(mappedBy = "petCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // @OrderColumn(name = "city_id") // city_id is a FK, using it as OrderColumn will corrupt the data
    private List<AdoptionPetArea> adoptionPetAreas;

    @OneToMany(mappedBy = "petCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "id")
    private List<LostNotification> lostNotifications;
}
