package com.rehome.main.service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rehome.main.dto.request.BannerRequest;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.BannerResponse;
import com.rehome.main.entity.Banner;
import com.rehome.main.repository.BannerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 輪播圖服務
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BannerService {

    private final BannerRepository bannerRepository;

    /**
     * 取得所有輪播圖（後台管理用）
     */
    public ApiResponse<BannerResponse.BannerList> getAllBanners() {
        try {
            List<Banner> banners = bannerRepository.findAllByOrderByIdAsc();
            
            List<BannerResponse> bannerResponses = banners.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            BannerResponse.BannerList result = BannerResponse.BannerList.builder()
                    .banners(bannerResponses)
                    .totalCount(bannerResponses.size())
                    .build();

            return ApiResponse.success("取得輪播圖列表成功", result);

        } catch (Exception e) {
            log.error("取得輪播圖列表失敗: {}", e.getMessage(), e);
            return ApiResponse.fail("系統錯誤，請稍後再試");
        }
    }

    /**
     * 取得啟用的輪播圖（前端首頁用）
     */
    public ApiResponse<List<BannerResponse.HomeBanner>> getActiveBanners() {
        try {
            List<Banner> banners = bannerRepository.findByIsActiveTrueOrderBySortOrderAsc();
            
            List<BannerResponse.HomeBanner> homeBanners = banners.stream()
                    .map(this::convertToHomeBanner)
                    .collect(Collectors.toList());

            log.info("取得啟用輪播圖 - 共 {} 筆", homeBanners.size());
            return ApiResponse.success("取得輪播圖成功", homeBanners);

        } catch (Exception e) {
            log.error("取得輪播圖失敗: {}", e.getMessage(), e);
            return ApiResponse.fail("系統錯誤，請稍後再試");
        }
    }

    /**
     * 取得單一輪播圖
     */
    public ApiResponse<BannerResponse> getBannerById(Integer id) {
        try {
            Banner banner = bannerRepository.findById(id)
                    .orElse(null);

            if (banner == null) {
                log.warn("查詢輪播圖失敗: ID {} 不存在", id);
                return ApiResponse.fail("輪播圖不存在");
            }

            BannerResponse response = convertToResponse(banner);
            return ApiResponse.success("取得輪播圖成功", response);

        } catch (Exception e) {
            log.error("取得輪播圖失敗: {}", e.getMessage(), e);
            return ApiResponse.fail("系統錯誤，請稍後再試");
        }
    }

    /**
     * 建立或更新輪播圖
     */
    @Transactional
    public ApiResponse<BannerResponse> saveBanner(Integer id, BannerRequest request) {
        log.info("開始儲存輪播圖 - ID: {}, Request: title={}, sortOrder={}, isActive={}", 
                id, request.getTitle(), request.getSortOrder(), request.getIsActive());
        
        Banner banner;
        boolean isNew = false;
        
        if (id != null) {
            // 嘗試查詢現有輪播圖
            banner = bannerRepository.findById(id).orElse(null);
            if (banner == null) {
                // ID 不存在，建立新的輪播圖（不設置 ID，讓資料庫自動產生）
                banner = new Banner();
                isNew = true;
                log.info("輪播圖 ID {} 不存在，建立新輪播圖", id);
            } else {
                log.info("更新現有輪播圖 - ID: {}", id);
            }
        } else {
            // 建立新輪播圖
            banner = new Banner();
            isNew = true;
            log.info("建立新輪播圖");
        }

        // 後蓋前檢查：只有在有提供新資料時才更新
        if (request.getBannerLg() != null && !request.getBannerLg().isEmpty()) {
            log.debug("更新大圖，Base64 長度: {}", request.getBannerLg().length());
            try {
                banner.setBannerLg(decodeBase64(request.getBannerLg()));
            } catch (IllegalArgumentException e) {
                log.error("大圖 Base64 解碼失敗: {}", e.getMessage());
                throw new IllegalArgumentException("大圖格式錯誤: " + e.getMessage());
            }
        } else if (isNew) {
            // 新建時如果沒有提供圖片，設為 null
            banner.setBannerLg(null);
        }
        // 否則保留原有的圖片資料
        
        if (request.getBannerSm() != null && !request.getBannerSm().isEmpty()) {
            log.debug("更新小圖，Base64 長度: {}", request.getBannerSm().length());
            try {
                banner.setBannerSm(decodeBase64(request.getBannerSm()));
            } catch (IllegalArgumentException e) {
                log.error("小圖 Base64 解碼失敗: {}", e.getMessage());
                throw new IllegalArgumentException("小圖格式錯誤: " + e.getMessage());
            }
        } else if (isNew) {
            banner.setBannerSm(null);
        }
        
        if (request.getTitle() != null) {
            banner.setTitle(request.getTitle());
        } else if (isNew) {
            banner.setTitle("");
        }
        
        if (request.getImageUrl() != null) {
            banner.setImageUrl(request.getImageUrl());
        } else if (isNew) {
            banner.setImageUrl("");
        }
        
        if (request.getLinkUrl() != null) {
            banner.setLinkUrl(request.getLinkUrl());
        } else if (isNew) {
            banner.setLinkUrl("");
        }
        
        if (request.getSortOrder() != null) {
            // 檢查排序權重是否重複
            List<Banner> duplicates = bannerRepository.findBySortOrderAndIdNot(
                request.getSortOrder(), 
                banner.getId() != null ? banner.getId() : 0
            );
            if (!duplicates.isEmpty()) {
                log.warn("排序權重 {} 已被使用", request.getSortOrder());
                throw new IllegalArgumentException("排序權重已被使用，請選擇其他數字");
            }
            banner.setSortOrder(request.getSortOrder());
        } else if (isNew) {
            banner.setSortOrder(0);
        }
        
        if (request.getIsActive() != null) {
            banner.setIsActive(request.getIsActive());
        } else if (isNew) {
            banner.setIsActive(true);
        }

        banner = bannerRepository.save(banner);

        log.info("輪播圖 {} 已儲存", banner.getId());
        
        BannerResponse response = convertToResponse(banner);
        return ApiResponse.success("輪播圖儲存成功", response);
    }

    /**
     * 刪除輪播圖
     */
    @Transactional
    public ApiResponse<Void> deleteBanner(Integer id) {
        try {
            if (!bannerRepository.existsById(id)) {
                return ApiResponse.fail("輪播圖不存在");
            }

            bannerRepository.deleteById(id);
            log.info("輪播圖 {} 已刪除", id);
            
            return ApiResponse.success("輪播圖刪除成功", null);

        } catch (Exception e) {
            log.error("刪除輪播圖失敗: {}", e.getMessage(), e);
            return ApiResponse.fail("系統錯誤，請稍後再試");
        }
    }

    /**
     * 轉換為完整回應 DTO
     */
    private BannerResponse convertToResponse(Banner banner) {
        return BannerResponse.builder()
                .id(banner.getId())
                .bannerLg(encodeBase64(banner.getBannerLg()))
                .bannerSm(encodeBase64(banner.getBannerSm()))
                .title(banner.getTitle())
                .imageUrl(banner.getImageUrl())
                .linkUrl(banner.getLinkUrl())
                .sortOrder(banner.getSortOrder())
                .isActive(banner.getIsActive())
                .build();
    }

    /**
     * 轉換為首頁輪播圖 DTO
     */
    private BannerResponse.HomeBanner convertToHomeBanner(Banner banner) {
        return BannerResponse.HomeBanner.builder()
                .id(banner.getId())
                .bannerLg(encodeBase64(banner.getBannerLg()))
                .bannerSm(encodeBase64(banner.getBannerSm()))
                .title(banner.getTitle())
                .linkUrl(banner.getLinkUrl())
                .build();
    }

    /**
     * Base64 編碼
     */
    private String encodeBase64(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Base64 解碼
     */
    private byte[] decodeBase64(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }
        try {
            // 移除 data:image/jpeg;base64, 前綴
            String cleanBase64 = base64String.replaceFirst("^data:image/[^;]+;base64,", "");
            return Base64.getDecoder().decode(cleanBase64);
        } catch (IllegalArgumentException e) {
            log.error("Base64 解碼失敗: {}", e.getMessage());
            throw new IllegalArgumentException("無效的圖片格式", e);
        }
    }
}
