package com.rehome.main.repository;

import java.util.List; // 👈 必須引用 Entity

import org.springframework.data.jpa.repository.JpaRepository; // 👈 必須引用 JPA
import org.springframework.stereotype.Repository;

import com.rehome.main.entity.CanMessage; // 👈 List 也是需要引用的

@Repository
public interface CanMessageRepo extends JpaRepository<CanMessage, Long> {
    
    // 找特定分類 (例如 "GREETING")
    List<CanMessage> findByType(String type);
}