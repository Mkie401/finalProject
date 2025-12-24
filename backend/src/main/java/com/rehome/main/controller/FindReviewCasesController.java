/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.rehome.main.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.response.PetReviewDto;
import com.rehome.main.service.FindReviewCasesService;

import lombok.RequiredArgsConstructor;



/**
 *送養 案件完整資料（審核頁用）
 * @author user
 */
@RestController
@RequestMapping("/api/se/")
@RequiredArgsConstructor
public class FindReviewCasesController {

   private final FindReviewCasesService reviewCasesService;

   @GetMapping("/adoption-cases/{caseId}")
    public ResponseEntity<Map<String, Object>> getCaseDetail(@PathVariable Long caseId) {
        try {
            PetReviewDto dto = reviewCasesService.getCaseDetail(caseId);
            
            // 建立符合前端期待的回應格式
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dto);
            response.put("message", "成功獲取案件資料");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 錯誤回應格式
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("data", null);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    


}
