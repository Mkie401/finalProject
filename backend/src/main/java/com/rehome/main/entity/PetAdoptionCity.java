package com.rehome.main.entity;

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

/**
 *
 * @author user
 */
@Entity
@Table(name="adoption_pet_area")
@Data
public class PetAdoptionCity {

    @EmbeddedId
    private PetAdoptionCityId paaid = new PetAdoptionCityId();
    
    // 關聯到 PetCase
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("caseId")
    @JoinColumn(name = "case_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Case petCase;
    
    // 關聯到 PetRegions
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cityId")
    @JoinColumn(name = "city_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private City perCity;

    public PetAdoptionCity() {
        this.paaid = new PetAdoptionCityId();}

}
