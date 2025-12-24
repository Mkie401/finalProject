package com.rehome.main.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.response.AnimalSpeciesResponse;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.CityOptionResponse;
import com.rehome.main.dto.response.RegionOptionResponse;
import com.rehome.main.repository.PetAnimalSpeciesRep;
import com.rehome.main.repository.PetCityRep;
import com.rehome.main.repository.PetRegionsRep;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/se")
@RequiredArgsConstructor
public class OptionsController {
    
    private final PetAnimalSpeciesRep animalSpeciesRep;
    private final PetRegionsRep regionsRep;
    private final PetCityRep  cityRep;
   

    /**
     * 取得所有動物種類選項
     * GET /api/se/animal-species
     */
    @GetMapping("/animal-species")
    public ResponseEntity<ApiResponse<List<AnimalSpeciesResponse>>> getAnimalSpecies() {
        List<AnimalSpeciesResponse> animalSpecies = animalSpeciesRep.findAll()
            .stream()
            .map(species -> new AnimalSpeciesResponse(species.getId(), species.getName()))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(animalSpecies));
    }

    /**
     * 取得所有縣市選項
     * GET /api/se/cities
     * 
     */
    @GetMapping("/cities")
    public ResponseEntity<ApiResponse<List<CityOptionResponse>>> getCities() {
        List<CityOptionResponse> cities = cityRep.findAll()
            .stream()
            .map(city -> new CityOptionResponse(city.getId(), city.getName()))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(cities));
    }

    /**
     * 取得指定縣市的地區選項
     * GET /api/se/regions?cityId=1
     */
    @GetMapping("/regions")
    public ResponseEntity<ApiResponse<List<RegionOptionResponse>>> getRegions(
            @RequestParam(required = false) Long cityId) {
        
        List<RegionOptionResponse> regions;
        
        if (cityId != null) {
            // 按縣市ID篩選區域
            regions = regionsRep.findByCityIdOrderByNameAsc(cityId)
                .stream()
                .map(region -> new RegionOptionResponse(region.getId(), region.getName()))
                .collect(Collectors.toList());
        } else {
            // 返回所有區域
            regions = regionsRep.findAll()
                .stream()
                .map(region -> new RegionOptionResponse(region.getId(), region.getName()))
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(ApiResponse.success(regions));
    }

}