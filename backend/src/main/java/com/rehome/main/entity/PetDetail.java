package com.rehome.main.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pet_detail")
@Data
public class PetDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK
    // private Long caseId;
    // private Long lostRegionId;
    @Column(name = "lost_date")
    private LocalDateTime lostDate;
    @Column(name = "lost_addr")
    private String lostAddr;

    @Column(precision = 10, scale = 6)
    private BigDecimal lng;

    @Column(precision = 10, scale = 6)
    private BigDecimal lat;
    @Column(name = "is_follow_ager")
    private Boolean isFollowAger;
    @Column(name = "is_family_ager")
    private Boolean isFamilyAger;
    @Column(name = "is_age_limit")
    private Boolean isAgeLimit;
    @Column(name = "adoption_requ")
    private String adoptionRequ;
    @Column(name = "lost_process")
    private String lostProcess;
    @Column(name = "medical_info")
    private String medicalInfo;
    @Column(name = "found_place")
    private String foundPlace;
    @Column(name = "entry_date")
    private LocalDateTime entryDate;
    private String description;

    // ------------------------------------
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", referencedColumnName = "id")
    @JsonBackReference
    private Case petCase; // 因為 case 是保留字，所以改成醬
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lost_region_id")
    private Region region;
}
