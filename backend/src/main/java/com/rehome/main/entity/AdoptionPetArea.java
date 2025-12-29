package com.rehome.main.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "adoption_pet_area")
@Data
public class AdoptionPetArea {

    @EmbeddedId
    private AdoptionPetAreaId id = new AdoptionPetAreaId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("caseId")
    @JoinColumn(name = "case_id")
    @JsonIgnoreProperties("adoptionPetAreas")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Case petCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cityId")
    @JoinColumn(name = "city_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private City city;
}
