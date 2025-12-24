package com.rehome.main.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rehome.main.dto.response.DashboardStatisticsResponse;
import com.rehome.main.dto.response.DashboardStatisticsResponse.AdoptionsStatistics;
import com.rehome.main.dto.response.DashboardStatisticsResponse.CasesStatistics;
import com.rehome.main.dto.response.DashboardStatisticsResponse.ListingsStatistics;
import com.rehome.main.dto.response.DashboardStatisticsResponse.ViewsStatistics;
import com.rehome.main.repository.CaseRepository;
import com.rehome.main.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儀表板統計服務
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final CaseRepository caseRepository;
    private final MemberRepository memberRepository;

    /**
     * 獲取儀表板統計數據
     */
    public DashboardStatisticsResponse getStatistics() {
        // log.info("開始獲取儀表板統計數據");

        DashboardStatisticsResponse statistics = new DashboardStatisticsResponse();
        
        // 1. 案例統計
        statistics.setCases(getCasesStatistics());
        
        // 2. 送養統計
        statistics.setAdoptions(getAdoptionsStatistics());
        
        // 3. 刊登統計
        statistics.setListings(getListingsStatistics());
        
        // 4. 登入瀏覽數統計
        statistics.setViews(getViewsStatistics());

        // log.info("儀表板統計數據獲取完成");
        return statistics;
    }

    /**
     * 獲取案例統計（依案例類型）
     * 案例類型（根據資料庫 case_type 表）：
     * - ID 1: 走失（對應 Dashboard 的「走失協尋」）
     * - ID 2: 送養（對應 Dashboard 的「送養」）
     * - ID 3: 收容所（對應 Dashboard 的「領養」）
     */
    private CasesStatistics getCasesStatistics() {
        try {
            // 統計各類型案例數量
            Long lostCount = caseRepository.countByCaseTypeId(1L);      // 走失
            Long adoptionCount = caseRepository.countByCaseTypeId(3L);  // 領養（收容所）
            Long surrenderCount = caseRepository.countByCaseTypeId(2L); // 送養
            Long total = lostCount + adoptionCount + surrenderCount;

            // log.info("案例統計 - 走失: {}, 領養: {}, 送養: {}, 總計: {}", 
            //         lostCount, adoptionCount, surrenderCount, total);

            return new CasesStatistics(lostCount, adoptionCount, surrenderCount, total);
        } catch (Exception e) {
            // log.error("獲取案例統計失敗", e);
            return new CasesStatistics(0L, 0L, 0L, 0L);
        }
    }

    /**
     * 獲取送養統計（本月與上月完成的送養案例）
     * 案例類型 2 = 送養
     */
    private AdoptionsStatistics getAdoptionsStatistics() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 本月區間
            LocalDateTime thisMonthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime thisMonthEnd = now;
            
            // 上月區間
            LocalDateTime lastMonthStart = thisMonthStart.minusMonths(1);
            LocalDateTime lastMonthEnd = thisMonthStart.minusSeconds(1);

            // 統計本月完成的送養案例（案例類型2且有結束日期）
            Long thisMonthCount = caseRepository.countByCaseTypeIdAndCaseDateEndBetween(
                    2L, thisMonthStart, thisMonthEnd);
            
            // 統計上月完成的送養案例
            Long lastMonthCount = caseRepository.countByCaseTypeIdAndCaseDateEndBetween(
                    2L, lastMonthStart, lastMonthEnd);
            
            Long change = thisMonthCount - lastMonthCount;

            // log.info("送養統計 - 本月: {}, 上月: {}, 變化: {}", thisMonthCount, lastMonthCount, change);

            return new AdoptionsStatistics(thisMonthCount, lastMonthCount, change);
        } catch (Exception e) {
            // log.error("獲取送養統計失敗", e);
            return new AdoptionsStatistics(0L, 0L, 0L);
        }
    }
    /**
     * 獲取刊登統計（本月與上月新刊登的送養案例）
     * 案例類型 2 = 送養
     */
    private ListingsStatistics getListingsStatistics() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 本月區間
            LocalDateTime thisMonthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime thisMonthEnd = now;
            
            // 上月區間
            LocalDateTime lastMonthStart = thisMonthStart.minusMonths(1);
            LocalDateTime lastMonthEnd = thisMonthStart.minusSeconds(1);

            // 統計本月刊登的送養案例（案例類型2且開始日期在本月）
            Long thisMonthCount = caseRepository.countByCaseTypeIdAndCaseDateStartBetween(
                    2L, thisMonthStart, thisMonthEnd);
            
            // 統計上月刊登的送養案例
            Long lastMonthCount = caseRepository.countByCaseTypeIdAndCaseDateStartBetween(
                    2L, lastMonthStart, lastMonthEnd);
            
            Long change = thisMonthCount - lastMonthCount;

            // log.info("刊登統計 - 本月: {}, 上月: {}, 變化: {}", thisMonthCount, lastMonthCount, change);

            return new ListingsStatistics(thisMonthCount, lastMonthCount, change);
        } catch (Exception e) {
            // log.error("獲取刊登統計失敗", e);
            return new ListingsStatistics(0L, 0L, 0L);
        }
    }
    

    /**
     * 獲取登入瀏覽數統計（過去6個月的會員登入數）
     */
    private ViewsStatistics getViewsStatistics() {
        try {
            List<String> labels = new ArrayList<>();
            List<Long> data = new ArrayList<>();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月");
            YearMonth currentMonth = YearMonth.now();

            // 統計過去6個月的數據
            for (int i = 5; i >= 0; i--) {
                YearMonth targetMonth = currentMonth.minusMonths(i);
                labels.add(targetMonth.format(formatter));
                
                // 統計該月份註冊的會員數量（模擬登入瀏覽數）
                LocalDateTime monthStart = targetMonth.atDay(1).atStartOfDay();
                LocalDateTime monthEnd = targetMonth.atEndOfMonth().atTime(23, 59, 59);
                
                Long count = memberRepository.countByCreatedAtBetween(monthStart, monthEnd);
                data.add(count);
            }

            // log.info("登入瀏覽數統計 - 標籤: {}, 數據: {}", labels, data);

            return new ViewsStatistics(labels, data);
        } catch (Exception e) {
            // log.error("獲取登入瀏覽數統計失敗", e);
            // 返回空數據
            return new ViewsStatistics(new ArrayList<>(), new ArrayList<>());
        }
    }
}
