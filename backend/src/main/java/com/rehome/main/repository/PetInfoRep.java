/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.rehome.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.PetInfo;

/**
 *
 * @author user
 */
public interface PetInfoRep extends JpaRepository<PetInfo, Long> {
    Optional<PetInfo> findFirstByPetCase_Id(Long caseId);
}
