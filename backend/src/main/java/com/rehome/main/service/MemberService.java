package com.rehome.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rehome.main.dto.request.UpdateMemberStatusRequest;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.MemberResponse;
import com.rehome.main.entity.Member;
import com.rehome.main.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 會員管理服務
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 取得會員列表（分頁 + 模糊搜尋）
     */
    public ApiResponse<MemberResponse.PagedMembers> getMembers(
            int page, int pageSize, String search) {
        try {
            // 建立分頁請求（依建立時間降序）
            Pageable pageable = PageRequest.of(
                page - 1, 
                pageSize, 
                Sort.by(Sort.Direction.DESC, "createdAt")
            );

            Page<Member> memberPage;

            // 判斷是否有搜尋關鍵字
            if (search != null && !search.trim().isEmpty()) {
                memberPage = memberRepository.searchMembers(search.trim(), pageable);
                log.info("搜尋會員: {} - 找到 {} 筆", search, memberPage.getTotalElements());
            } else {
                memberPage = memberRepository.findAll(pageable);
                log.info("取得全部會員 - 共 {} 筆", memberPage.getTotalElements());
            }

            // 轉換為 DTO
            var memberResponses = memberPage.getContent().stream()
                    .map(this::convertToResponse)
                    .toList();

            // 建立分頁資訊
            var paginationInfo = MemberResponse.PaginationInfo.builder()
                    .currentPage(page)
                    .pageSize(pageSize)
                    .totalPages(memberPage.getTotalPages())
                    .totalRecords(memberPage.getTotalElements())
                    .build();

            // 建立回應
            var pageResponse = MemberResponse.PagedMembers.builder()
                    .data(memberResponses)
                    .pagination(paginationInfo)
                    .build();

            return ApiResponse.success("取得會員列表成功", pageResponse);

        } catch (Exception e) {
            log.error("取得會員列表失敗: {}", e.getMessage(), e);
            return ApiResponse.fail("系統錯誤，請稍後再試");
        }
    }

    /**
     * 取得單一會員詳細資料
     */
    public ApiResponse<MemberResponse> getMemberById(Long id) {
        try {
            Member member = memberRepository.findById(id)
                    .orElse(null);

            if (member == null) {
                log.warn("查詢會員失敗: ID {} 不存在", id);
                return ApiResponse.fail("會員不存在");
            }

            MemberResponse response = convertToResponse(member);
            return ApiResponse.success("取得會員資料成功", response);

        } catch (Exception e) {
            log.error("取得會員資料失敗: {}", e.getMessage(), e);
            return ApiResponse.fail("系統錯誤，請稍後再試");
        }
    }

    /**
     * 更新會員狀態（啟用/凍結）
     */
    @Transactional
    public ApiResponse<Void> updateMemberStatus(Long id, UpdateMemberStatusRequest request) {
        try {
            Member member = memberRepository.findById(id)
                    .orElse(null);

            if (member == null) {
                log.warn("更新會員狀態失敗: ID {} 不存在", id);
                return ApiResponse.fail("會員不存在");
            }

            String oldStatus = member.getStatus();
            member.setStatus(request.getStatus());
            memberRepository.save(member);

            log.info("會員 {} ({}) 狀態已更新: {} -> {}", 
                    member.getName(), member.getEmail(), oldStatus, request.getStatus());

            String message = "active".equals(request.getStatus()) ? 
                    "會員帳號已啟用" : "會員帳號已凍結";
            return ApiResponse.success(message, null);

        } catch (Exception e) {
            log.error("更新會員狀態失敗: {}", e.getMessage(), e);
            return ApiResponse.fail("系統錯誤，請稍後再試");
        }
    }

    /**
     * 轉換 Entity 為 Response DTO
     */
    private MemberResponse convertToResponse(Member member) {
        // 性別轉換
        String genderText = null;
        if (member.getGender() != null) {
            genderText = member.getGender() ? "男" : "女";
        }

        // 生日格式化
        String birthdayText = null;
        if (member.getBirthDate() != null) {
            birthdayText = new java.text.SimpleDateFormat("yyyy/MM/dd")
                    .format(member.getBirthDate());
        }

        // 手機號碼格式化（加上連字號）
        String phoneText = member.getPhone();
        if (phoneText != null && phoneText.length() == 10) {
            phoneText = phoneText.substring(0, 4) + "-" + phoneText.substring(4);
        }

        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickName())
                .gender(genderText)
                .birthday(birthdayText)
                .phone(phoneText)
                .address(null) // 資料庫暫無地址欄位
                .status(member.getStatus())
                .role(member.getRole())
                .build();
    }
}
