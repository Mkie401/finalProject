package com.rehome.main.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 *
 * @author user
 */
@Embeddable
@Data
public class PetAdoptionCityId implements Serializable {
    
    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "city_id")
    private Long cityId;
}
