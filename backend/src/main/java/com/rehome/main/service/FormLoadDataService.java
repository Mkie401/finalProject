package com.rehome.main.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rehome.main.dto.response.CitySimpleResponse;
import com.rehome.main.dto.response.FormLoadDataResponse;
import com.rehome.main.dto.response.RegionSimpleResponse;
import com.rehome.main.entity.AnimalSpecies;
import com.rehome.main.entity.City;
import com.rehome.main.entity.Region;
import com.rehome.main.repository.AnimalSpeciesRepo;
import com.rehome.main.repository.CityRepo;
import com.rehome.main.repository.RegionRepo;

@Service
public class FormLoadDataService {

    @Autowired private CityRepo cityRepo;
    @Autowired private RegionRepo regionRepo; 
    @Autowired private AnimalSpeciesRepo animalSpeciesRepo;

    @Transactional(readOnly = true)
    public FormLoadDataResponse getAllFormOptions() {
        // 撈出所有資料
        List<City> cityEntities = cityRepo.findAll();
        List<Region> regionEntities = regionRepo.findAll();
        List<AnimalSpecies> species = animalSpeciesRepo.findAll();
        // 轉換 City -> CitySimpleDto (去掉 regions 陣列)
        List<CitySimpleResponse> cityDtos = cityEntities.stream()
                .map(c -> new CitySimpleResponse(c.getId(), c.getName()))
                .collect(Collectors.toList());

        // 轉換 Region -> RegionSimpleDto (把 city 物件變成只有 cityId)
        List<RegionSimpleResponse> regionDtos = regionEntities.stream()
                .map(r -> new RegionSimpleResponse(
                    r.getId(), 
                    r.getCity().getId(), 
                    r.getName()
                ))
                .collect(Collectors.toList());
        // 包裝成 DTO 回傳
        return new FormLoadDataResponse(cityDtos, regionDtos, species);
    }

}
