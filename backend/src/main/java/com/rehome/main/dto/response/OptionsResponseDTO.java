package com.rehome.main.dto.response;

import java.util.List;

import com.rehome.main.entity.AnimalSpecies;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptionsResponseDTO {
    private List<CityDTO> citys;
    private List<ShelterDTO> shelters;
    private List<AnimalSpecies> species;

    @Data
    @Builder
    public static class CityDTO {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    public static class ShelterDTO {
        private Long id;
        private String name;
    }
    
}
