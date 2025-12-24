package com.rehome.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.request.UpdateMemberStatusRequest;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.MemberResponse;
import com.rehome.main.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 會員管理控制器
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MemberController {

    private final MemberService memberService;

    /**
     * 取得會員列表（分頁 + 模糊搜尋）
     * GET /api/members?page=1&pageSize=10&search=關鍵字
     */
    @GetMapping
    public ResponseEntity<ApiResponse<MemberResponse.PagedMembers>> getMembers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search) {
        
        log.info("取得會員列表 - 頁碼: {}, 每頁: {}, 搜尋: {}", page, pageSize, search);

        // 驗證分頁參數
        if (page < 1) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("頁碼必須大於 0"));
        }

        if (pageSize < 1 || pageSize > 100) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("每頁筆數必須介於 1-100 之間"));
        }

        ApiResponse<MemberResponse.PagedMembers> response = 
                memberService.getMembers(page, pageSize, search);

        return ResponseEntity.ok(response);
    }

    /**
     * 取得單一會員詳細資料
     * GET /api/members/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(
            @PathVariable Long id) {
        
        log.info("取得會員資料 - ID: {}", id);

        ApiResponse<MemberResponse> response = memberService.getMemberById(id);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * 更新會員狀態（啟用/凍結）
     * PUT /api/members/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateMemberStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberStatusRequest request,
            BindingResult bindingResult) {
        
        log.info("更新會員狀態 - ID: {}, 新狀態: {}", id, request.getStatus());

        // 驗證請求參數
        if (bindingResult.hasErrors()) {
            var fieldError = bindingResult.getFieldError();
            String errorMessage = (fieldError != null && fieldError.getDefaultMessage() != null)
                    ? fieldError.getDefaultMessage()
                    : "請求參數錯誤";
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(errorMessage));
        }

        ApiResponse<Void> response = memberService.updateMemberStatus(id, request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(response);
        }
    }
}
