package com.rehome.main.dto.response;

import lombok.Data;

@Data
public class RegionOptionResponse {
    private Long id;
    private String name;
    
    public RegionOptionResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}