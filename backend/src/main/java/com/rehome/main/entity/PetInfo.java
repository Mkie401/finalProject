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
@Table(name = "pet_info")
@Data
public class PetInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK
    // private long caseId;
    // private long petStatusId;
    // private long regionId;
    // private long animalSpeciesId;

    private String name;
    @Column(name = "animal_species_other")
    private String animalSpeciesOther;
    private String gender;
    private String breed;
    private String color;
    private String size;
    private String age;
    private String feature;
    @Column(name = "is_ear_tipping")
    private Boolean isEarTipping;
    @Column(name = "is_chip")
    private Boolean isChip;
    @Column(name = "chip_number")
    private String chipNumber;

    
    // ------------------------------------
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", referencedColumnName = "id")
	@JsonBackReference
    private Case petCase;       // 因為 case 是保留字，所以改成醬
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_species_id")
    private AnimalSpecies animalSpecies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", referencedColumnName = "id")
    private Region region;
}
