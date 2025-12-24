package com.rehome.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.Shelter;

public interface ShelterRepository extends JpaRepository<Shelter, Long> {

}
