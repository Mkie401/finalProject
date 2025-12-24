package com.rehome.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.AnimalSpecies;

public interface SpeciesRepository extends JpaRepository<AnimalSpecies, Long> {

}
