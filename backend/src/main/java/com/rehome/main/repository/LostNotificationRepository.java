package com.rehome.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.LostNotification;

public interface LostNotificationRepository extends JpaRepository<LostNotification, Long> {
    Page<LostNotification> findByPetCase_Member_IdAndPetCase_CaseNumber(Long memberId, String caseNumber, Pageable pageable);
}
