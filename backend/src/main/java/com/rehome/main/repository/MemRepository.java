package com.rehome.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.MemEntity;

public interface MemRepository extends JpaRepository<MemEntity, Integer> {
    // 檢查 email 是否存在
    boolean existsByEmail(String email);

    // 根據 email 查找會員
    MemEntity findByEmail(String email);
}

