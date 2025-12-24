package com.rehome.main.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 輪播圖請求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BannerRequest {

    @Size(max = 100, message = "標題長度不得超過 100 個字元")
    private String title;

    @Size(max = 255, message = "圖片路徑長度不得超過 255 個字元")
    private String imageUrl;

    @Size(max = 255, message = "連結網址長度不得超過 255 個字元")
    private String linkUrl;

    @Min(value = 0, message = "排序權重必須大於等於 0")
    private Integer sortOrder;

    private String bannerLg;  // Base64 編碼的大圖

    private String bannerSm;  // Base64 編碼的小圖

    private Boolean isActive;
}
