/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.rehome.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.Case;

/**
 *
 * @author user
 */
public interface PetCaseRep extends JpaRepository<Case, Long> {
    List<Case> findByMember_Id(Long memberId);
    List<Case> findByMember_IdAndCaseType_Id(Long memberId, Long CaseTypeId);
    Optional<Case> findByIdAndMember_Id(Long caseId, Long memberId);
}
