package com.rehome.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rehome.main.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>, JpaSpecificationExecutor<Favorite> {
    // 用案件id 判斷狀態
    boolean existsByMemberIdAndPetCaseId(Long memberId, Long petCaseId);

    // 用案件編號 判斷狀態
    boolean existsByMemberIdAndPetCaseCaseNumber(Long memberId, String caseNumber);

    Optional<Favorite> findByMemberIdAndPetCaseCaseNumber(Long memberId, String caseNumber);

    // 因為用原生的砍不掉，所以就自定義方法
    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.member.id = :memberId AND f.petCase.caseNumber = :caseNumber")
    int deleteByMemberIdAndPetCaseCaseNumber(@Param("memberId") Long memberId, @Param("caseNumber") String caseNumber);

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
    List<Favorite> findByMemberIdAndPetCase_CaseTypeIdInAndPetCase_CaseStatusIdIn(Long memberId, List<Long> caseTypeIds, List<Long> caseStatusId, Pageable pageable);
}
