package com.rehome.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.AnimalSpecies;

public interface AnimalSpeciesRepo extends JpaRepository<AnimalSpecies, Long>{
    Optional<AnimalSpecies> findByName(String name);
}
