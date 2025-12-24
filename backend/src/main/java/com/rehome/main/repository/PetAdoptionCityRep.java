package com.rehome.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rehome.main.entity.PetAdoptionCity;
import com.rehome.main.entity.PetAdoptionCityId;

@Repository
public interface PetAdoptionCityRep extends JpaRepository<PetAdoptionCity, PetAdoptionCityId> {

}