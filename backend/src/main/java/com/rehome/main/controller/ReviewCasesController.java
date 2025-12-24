package com.rehome.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.request.ChangeCaseStatusRequest;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.ReviewCaseResponse;
import com.rehome.main.service.ReviewCasesService;

@RequestMapping("/api/review")
@RestController
public class ReviewCasesController {

    @Autowired
    private ReviewCasesService reviewCasesService;

    // 改變案號審核狀態
    @PatchMapping("/{caseNumber}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> changeStatus(
        @RequestBody ChangeCaseStatusRequest request,
        @PathVariable String caseNumber) {

        reviewCasesService.changeCaseStatus(caseNumber, request);
        
        return ApiResponse.success("更改成功"); // 回傳成功，不需帶資料
    }
    // 抓所有待審核
    @GetMapping("/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ReviewCaseResponse>> getMemberCases(@PathVariable Long status) {
        List<ReviewCaseResponse> result = reviewCasesService.findCasesByStatus(status);
        
        return ApiResponse.success(result);
    }

}
