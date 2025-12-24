package com.rehome.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rehome.main.entity.PasswordResetTokenEntity;
// 12/9
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Integer> {
    
    // 根據 Token 查詢
    Optional<PasswordResetTokenEntity> findByToken(String token);
    
    // 根據 Email 查詢最新的未使用 Token
    @Query("""
        SELECT p 
        FROM PasswordResetTokenEntity p 
        WHERE p.email = ?1 AND p.used = false 
        ORDER BY p.createdAt DESC
        """)
    Optional<PasswordResetTokenEntity> findLatestUnusedByEmail(String email);
    
    // 刪除指定 Email 的所有 Token
    void deleteByEmail(String email);
}
