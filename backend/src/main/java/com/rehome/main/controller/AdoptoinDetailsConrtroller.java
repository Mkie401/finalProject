package com.rehome.main.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.service.AdoptoinDetailsService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/pu")
@RequiredArgsConstructor
public class AdoptoinDetailsConrtroller {
    // 會員中心 - 刊登送養詳情
    private final AdoptoinDetailsService adoptoinDetailsService;

    @GetMapping("members/{memberId}/adoptions")
    public ResponseEntity<Map<String, Object>> getAdoptionDetails(
            @PathVariable Long memberId) {

    //     @GetMapping("/member/adoptions")
    // public ResponseEntity<Map<String, Object>> getMyAdoptionDetails(HttpServletRequest request) {

    //     // 從 JwtAuthenticationFilter 放進來的 attribute 拿 memberId
    //     Long memberId = (Long) request.getAttribute("memberId");

    //     if (memberId == null) {
    //         // 沒有驗證成功（沒帶 Token 或 Token 壞掉）
    //         Map<String, Object> error = Map.of(
    //             "success", false,
    //             "message", "尚未登入或憑證已失效"
    //         );
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    //     }

        
        var adoptionDetails = adoptoinDetailsService.getAllAdoptionDetails(memberId);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "data", adoptionDetails
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/member/adoptions/{caseId}/terminate")
     public ResponseEntity<Map<String, Object>> terminateAdoptionCase(
            @PathVariable Long caseId,
            HttpServletRequest request){
                
        Long memberId = (Long) request.getAttribute("memberId");
            if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("success", false, "message", "尚未登入")
            );
        }
        adoptoinDetailsService.terminateAdoptionCase(caseId, memberId);
        return ResponseEntity.ok(
            Map.of("success", true, "message", "結案成功")
        );
     }

}
