package com.rehome.main.dto.response;

import java.util.List;

import com.rehome.main.entity.AnimalSpecies;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FormLoadDataResponse {
    private List<CitySimpleResponse> cities;
    private List<RegionSimpleResponse> regions; // 注意這裡改用 DTO
    private List<AnimalSpecies> species;
}
