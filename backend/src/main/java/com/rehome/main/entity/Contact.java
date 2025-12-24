package com.rehome.main.entity;

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
@Table(name = "contact")
@Data
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //FK
    // private long caseId;
    // private long shelterId;
    
    private String name;
    private String tel;
    private String mail;
    @Column(name="other_contact")
    private String otherContact;
    @Column(name = "is_phone_display")
    private Boolean isPhoneDisplay;
    @Column(name = "is_email_display")
    private Boolean isEmailDisplay;
    

    
    // ------------------------------------
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", referencedColumnName = "id")
	@JsonBackReference
    private Case petCase;       // 因為 case 是保留字，所以改成醬

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id")
    private Shelter shelter;
}
