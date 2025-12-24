package com.rehome.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rehome.main.entity.Case;

@Repository
public interface CaseRepo extends JpaRepository<Case, Long>{
    Optional<Case> findByCaseNumber(String caseNumber);

    Optional<Case> findByCaseNumberAndMemberId(String caseNumber, Long memberId);
    List<Case> findByMemberIdAndCaseTypeId(Long memberId, Long caseTypeId);
    
    @EntityGraph(attributePaths = {"petInfo", "petDetail", "contact"})
    List<Case> findByCaseStatus_Id(Long id);

    List<Case> findByMember_Id(Long memberId);

}
