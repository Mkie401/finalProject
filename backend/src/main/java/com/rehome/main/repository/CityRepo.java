package com.rehome.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rehome.main.entity.City;

public interface CityRepo extends JpaRepository<City, Long>{
    @Query("SELECT DISTINCT c FROM City c LEFT JOIN FETCH c.regions")
    List<City> findAllWithRegions();
}
