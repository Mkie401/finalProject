package com.rehome.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehome.main.entity.CustomerServiceForm;

public interface CustomerServiceFormRepo extends JpaRepository<CustomerServiceForm, Long>{
    // 根據狀態查詢
    List<CustomerServiceForm> findByStatusOrderByCreatedTime(String status);
}
