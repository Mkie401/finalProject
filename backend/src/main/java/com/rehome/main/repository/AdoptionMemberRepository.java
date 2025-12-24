package com.rehome.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.AdoptionMember;

public interface AdoptionMemberRepository extends JpaRepository<AdoptionMember, Long> {
    // check
    boolean existsByMemberIdAndPetCase_CaseNumber(Long memberId, String CaseNumber);

    // 同時間最多只能領養三隻
    int countByMemberIdAndAdoptionStatusIdIn(Long memberId, List<Long> adoptionStatusIds);

    @EntityGraph(attributePaths = {
        "petCase.caseType", 
        "petCase.petInfo", 
        "petCase.petInfo.animalSpecies", 
        "petCase.petInfo.region", 
        "petCase.petInfo.region.city",
        "petCase.petDetail",
        "petCase.petDetail.region", 
        "petCase.petDetail.region.city", 
        "petCase.contact",
        "petCase.contact.shelter",
        "petCase.petImage",
        "petCase.adoptionMembers"
    })
    List<AdoptionMember> findByMemberId(Long memberId);
}
