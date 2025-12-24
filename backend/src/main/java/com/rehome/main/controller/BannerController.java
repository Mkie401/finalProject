package com.rehome.main.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.request.BannerRequest;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.BannerResponse;
import com.rehome.main.service.BannerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 輪播圖控制器
 */
@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BannerController {

    private final BannerService bannerService;

    /**
     * 取得所有輪播圖（後台管理用）
     * GET /api/banners
     */
    @GetMapping
    public ResponseEntity<ApiResponse<BannerResponse.BannerList>> getAllBanners() {
        ApiResponse<BannerResponse.BannerList> response = bannerService.getAllBanners();
        return ResponseEntity.ok(response);
    }

    /**
     * 取得啟用的輪播圖（前端首頁用）
     * GET /api/banners/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<BannerResponse.HomeBanner>>> getActiveBanners() {
        ApiResponse<List<BannerResponse.HomeBanner>> response = bannerService.getActiveBanners();
        return ResponseEntity.ok(response);
    }

    /**
     * 取得單一輪播圖
     * GET /api/banners/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BannerResponse>> getBannerById(@PathVariable Integer id) {
        ApiResponse<BannerResponse> response = bannerService.getBannerById(id);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * 建立或更新輪播圖
     * POST /api/banners/{id}
     */
    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<BannerResponse>> saveBanner(
            @PathVariable Integer id,
            @Valid @RequestBody BannerRequest request,
            BindingResult bindingResult) {
        
        // 驗證請求參數
        if (bindingResult.hasErrors()) {
            var fieldError = bindingResult.getFieldError();
            String errorMessage = (fieldError != null && fieldError.getDefaultMessage() != null)
                    ? fieldError.getDefaultMessage()
                    : "請求參數錯誤";
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(errorMessage));
        }

        try {
            ApiResponse<BannerResponse> response = bannerService.saveBanner(id, request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (IllegalArgumentException e) {
            log.error("儲存輪播圖失敗 - 參數錯誤: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("儲存輪播圖失敗: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("系統錯誤，請稍後再試"));
        }
    }

    /**
     * 刪除輪播圖
     * DELETE /api/banners/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Integer id) {
        ApiResponse<Void> response = bannerService.deleteBanner(id);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(response);
        }
    }
}
