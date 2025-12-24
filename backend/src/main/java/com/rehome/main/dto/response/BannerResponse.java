package com.rehome.main.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 輪播圖回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerResponse {

    private Integer id;
    private String bannerLg;  // Base64 編碼
    private String bannerSm;  // Base64 編碼
    private String title;
    private String imageUrl;
    private String linkUrl;
    private Integer sortOrder;
    private Boolean isActive;

    /**
     * 前端首頁用的輪播圖 DTO（簡化版）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeBanner {
        private Integer id;
        private String bannerLg;  // Base64 編碼
        private String bannerSm;  // Base64 編碼
        private String title;
        private String linkUrl;
    }

    /**
     * 輪播圖列表回應
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BannerList {
        private List<BannerResponse> banners;
        private Integer totalCount;
    }
}
