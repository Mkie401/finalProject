package com.rehome.main.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionPetAreaId implements Serializable {
    private Case petCase;
    private City city;
}
