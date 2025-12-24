package com.rehome.main.controller;

import com.rehome.main.dto.response.DashboardStatisticsResponse;
import com.rehome.main.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 儀表板統計 API Controller
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 獲取儀表板統計數據
     * 
     * @return 儀表板統計數據
     */
    @GetMapping("/statistics")
    public ResponseEntity<DashboardStatisticsResponse> getStatistics() {
        // log.info("收到儀表板統計數據請求");
        
        try {
            DashboardStatisticsResponse statistics = dashboardService.getStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            // log.error("獲取儀表板統計數據失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
