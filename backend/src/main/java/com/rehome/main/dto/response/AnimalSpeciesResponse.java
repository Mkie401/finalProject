package com.rehome.main.dto.response;

import lombok.Data;

@Data
public class AnimalSpeciesResponse {
    private Long id;
    private String name;
    
    public AnimalSpeciesResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}