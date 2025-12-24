package com.rehome.main.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rehome.main.entity.MemOtpEntity;

@Repository
public interface  MemOtpRepository extends JpaRepository<MemOtpEntity, Integer>{
    
    // 根據 email 查找未驗證的最新 OTP 記錄
    @Query("""
            SELECT o FROM MemOtpEntity o
            WHERE o.email = :email AND o.verified = false
            ORDER BY o.createdAt DESC
            """)
    Optional<MemOtpEntity> findLatestUnverifiedByEmail(@Param("email") String email);

    // 根據 email 刪除 OTP 記錄
    void deleteByEmail(String email);

    // 刪除過期的 OTP 記錄
    void deleteByExpiresAtBefore(LocalDateTime dateTime);

}
