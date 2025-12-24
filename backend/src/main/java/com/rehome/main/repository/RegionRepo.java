package com.rehome.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.Region;

public interface RegionRepo extends JpaRepository<Region, Long>{
     Optional<Region> findByName(String name);
}
