package com.rehome.main.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.rehome.main.entity.Case;


public interface CaseRepository extends JpaRepository<Case, Long>, JpaSpecificationExecutor<Case> {
    Page<Case> findByCaseTypeIdAndCaseDateEndIsNullOrderByCaseDateStartDesc(Long caseTypeId, Pageable pageable);

    // home
    @EntityGraph(attributePaths = {
        "caseType",
        "petInfo",
        "petInfo.animalSpecies",
        "petInfo.region",
        "petInfo.region.city",
        "petDetail",
        "petDetail.region",
        "petDetail.region.city",
        "contact",
        "petImage",
        "adoptionMembers"
    })
    List<Case> findTop10ByCaseTypeIdInAndCaseStatusIdOrderByCaseDateStartDesc(List<Long> caseTypeIds, Long caseStatusId);

    @Override
    @EntityGraph(attributePaths = {
        "caseType", 
        "petInfo", 
        "petInfo.animalSpecies", 
        "petInfo.region", 
        "petInfo.region.city",
        "petDetail",
        "petDetail.region", 
        "petDetail.region.city", 
        "contact",
        "contact.shelter",
        "petImage",
        "adoptionMembers"
    })
    Page<Case> findAll(Specification<Case> spec, Pageable pageable);

    // CasePage
    @EntityGraph(attributePaths = {
        "caseType", 
        "petInfo", 
        "petInfo.animalSpecies", 
        "petInfo.region", 
        "petInfo.region.city",
        "petDetail",
        "petDetail.region", 
        "petDetail.region.city", 
        "contact",
        "contact.shelter",
        "petImage",
        "adoptionMembers",
        "adoptionPetAreas",
        "adoptionPetAreas.city"
    })
    Case findByCaseNumberAndCaseStatusId(String caseNumber, Long caseStatusId);

    Case getReferenceByCaseNumber(String caseNumber);
    
    // Dashboard 統計方法
    Long countByCaseTypeId(Long caseTypeId);
    
    Long countByCaseTypeIdAndCaseDateStartBetween(Long caseTypeId, LocalDateTime start, LocalDateTime end);
    
    Long countByCaseTypeIdAndCaseDateEndBetween(Long caseTypeId, LocalDateTime start, LocalDateTime end);

    Optional<Case> findByCaseNumber(String caseNumber);
}
