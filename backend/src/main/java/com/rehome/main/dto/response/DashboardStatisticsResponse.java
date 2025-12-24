package com.rehome.main.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 儀表板統計資料 Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsResponse {
    private CasesStatistics cases;
    private AdoptionsStatistics adoptions;
    private ListingsStatistics listings;
    private ViewsStatistics views;

    /**
     * 案例統計
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CasesStatistics {
        private Long lost;       // 走失協尋
        private Long adoption;   // 領養
        private Long surrender;  // 送養
        private Long total;      // 總計
    }

    /**
     * 送養統計
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdoptionsStatistics {
        private Long thisMonth;  // 本月送養
        private Long lastMonth;  // 上月送養
        private Long change;     // 變化量
    }

    /**
     * 刊登統計
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListingsStatistics {
        private Long thisMonth;  // 本月刊登(送養)
        private Long lastMonth;  // 上月刊登(送養)
        private Long change;     // 變化量
    }

    /**
     * 瀏覽數統計
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ViewsStatistics {
        private List<String> labels;  // 月份標籤
        private List<Long> data;      // 登入瀏覽數
    }
}
