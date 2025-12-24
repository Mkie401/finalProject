package com.rehome.main.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 會員管理回應 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {

    private Long id;
    private String email;
    private String name;
    private String nickname;
    private String gender;
    private String birthday;
    private String phone;
    private String address;
    private String status;
    private String role;

    /**
     * 分頁會員列表回應
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PagedMembers {
        private List<MemberResponse> data;
        private PaginationInfo pagination;
    }

    /**
     * 分頁資訊
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationInfo {
        private int currentPage;
        private int pageSize;
        private int totalPages;
        private long totalRecords;
    }
}
