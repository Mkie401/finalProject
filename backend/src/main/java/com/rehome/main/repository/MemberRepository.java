package com.rehome.main.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rehome.main.entity.Member;

/**
 * 會員資料存取介面
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 根據 Email 查詢會員
     */
    Optional<Member> findByEmail(String email);

    /**
     * 檢查 Email 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 統計指定時間區間內註冊的會員數量
     */
    @Query("SELECT COUNT(m) FROM Member m WHERE m.createdAt BETWEEN :startDate AND :endDate")
    Long countByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 模糊搜尋會員（依 Email、姓名、暱稱）並分頁
     */
    @Query("SELECT m FROM Member m WHERE " +
           "LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.nickName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Member> searchMembers(@Param("keyword") String keyword, Pageable pageable);
}
