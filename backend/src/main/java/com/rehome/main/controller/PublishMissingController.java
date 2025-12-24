package com.rehome.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.request.MissingPublishRequest;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.CaseCreationResponse;
import com.rehome.main.dto.response.CaseStatusResponse;
import com.rehome.main.dto.response.MissingInfoResponse;
import com.rehome.main.entity.Case;
import com.rehome.main.repository.MemberRepository;
import com.rehome.main.service.MissingPublishService;
import com.rehome.main.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/api/missing")
@RestController
public class PublishMissingController {

    @Autowired
    private MissingPublishService missingPublishService;
    @Autowired
    private MemberRepository memberRepository;

// 模擬登入用
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ApiResponse<?> publishMissingCase(
            @RequestBody MissingPublishRequest request,
           HttpServletRequest httprequest) { // 假設 id 存在 session

        Long memberId = (Long) httprequest.getAttribute("memberId");
        
        // 雙重保險：雖然 PreAuthorize 擋在前面，但防呆還是檢查一下
        if (memberId == null) {
            return ApiResponse.fail("認證資訊錯誤：找不到會員ID");
        }
        

        try {

            Case savedCase = missingPublishService.createMissingCase(request, memberId);

            CaseCreationResponse responseData = new CaseCreationResponse(savedCase.getCaseNumber());

            return ApiResponse.success("案件發布成功", responseData);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail("發布失敗，請再試一次");
        }
    }

    @GetMapping("/{caseNumber}/status")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ApiResponse<?> getCaseStatus(@PathVariable String caseNumber, HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("memberId");
        
        // 防呆
        if (memberId == null) {
            return ApiResponse.fail("認證資訊錯誤：找不到會員ID");
        }
        
        // Long memberID = 5L;
        CaseStatusResponse statusDto = missingPublishService.getCaseStatus(caseNumber, memberId);
        // Long sessionMemberId = (Long) session.getAttribute("memberId");

        if (statusDto == null) {
            return ApiResponse.fail("查不到該筆案號");
        } else {
            // if (!sessionMemberId.equals(statusDto.getMemberId())) {
            //     return ApiResponse.fail("權限不足");
            // }
            return ApiResponse.success("查詢成功", statusDto);
        }

    }
    //模擬登入

   @GetMapping("/test/login")
    public String testLogin(@RequestParam Long id) {
        
        // 2. 準備假資料 (因為 JWT 裡面通常會包 Email 和 Role)
        // 你的 Filter 會讀取這些資訊，所以測試時也要塞進去
        String fakeEmail = "user" + id + "@example.com";
        String fakeRole = "MEMBER"; // 或是 "ADMIN"，看你想測什麼身分
        
        // 3. 呼叫 JwtUtil 產生 Token
        // 【注意】：請確認你的 JwtUtil 裡面產生 Token 的方法名稱
        // 根據你 Filter 的用法，這裡應該要傳入 id, email, role
        String token = jwtUtil.generateAccessToken(id, fakeEmail, fakeRole); 
        
        // 4. 直接回傳 Token 字串
        return token;
    }

    // 走失找到了 結案
    @PatchMapping("/founded/{caseNumber}")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ApiResponse<?> caseClose(@PathVariable String caseNumber, HttpServletRequest request) {
       
       Long memberId = (Long) request.getAttribute("memberId");

        // 防呆：如果沒登入或 Token 有問題，這裡可能會是 null
        if (memberId == null) {
            throw new RuntimeException("無法取得使用者 ID，請重新登入");
        }


        try {
            missingPublishService.changeCaseStatus(caseNumber, memberId);
            return ApiResponse.success("結案成功");
        } catch (Exception e) {
            return ApiResponse.fail("請重試" + e);
        }

    }
    // 抓所有單一會員 走失案件

    @GetMapping("/cases")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ApiResponse<List<MissingInfoResponse>> getMemberCases(HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("memberId");

        // 防呆：如果沒登入或 Token 有問題，這裡可能會是 null
        if (memberId == null) {
            throw new RuntimeException("無法取得使用者 ID，請重新登入");
        }

        // Long sessionMemberId = (Long) session.getAttribute("memberId");
        List<MissingInfoResponse> dto = missingPublishService.getMissingCaseInfo(memberId);

        return ApiResponse.success("查詢成功", dto);
    }

}
