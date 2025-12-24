package com.rehome.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rehome.main.entity.Banner;

/**
 * 輪播圖 Repository
 */
@Repository
public interface BannerRepository extends JpaRepository<Banner, Integer> {
    
    /**
     * 查詢所有輪播圖，按 ID 升序排列
     */
    List<Banner> findAllByOrderByIdAsc();
    
    /**
     * 查詢所有啟用的輪播圖，按排序權重升序排列
     */
    List<Banner> findByIsActiveTrueOrderBySortOrderAsc();
    
    /**
     * 查詢指定排序權重的輪播圖（用於檢查重複）
     */
    List<Banner> findBySortOrderAndIdNot(Integer sortOrder, Integer id);
}
