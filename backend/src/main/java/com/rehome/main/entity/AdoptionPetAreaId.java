package com.rehome.main.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionPetAreaId implements Serializable {

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "city_id")
    private Long cityId;
}
