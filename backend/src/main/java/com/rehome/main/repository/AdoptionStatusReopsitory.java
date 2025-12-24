package com.rehome.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.AdoptionStatus;

public interface AdoptionStatusReopsitory extends JpaRepository<AdoptionStatus, Long> {

}
