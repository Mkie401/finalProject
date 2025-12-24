package com.rehome.main.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新會員狀態請求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberStatusRequest {

    @NotBlank(message = "狀態不得為空")
    @Pattern(regexp = "^(active|block)$", message = "狀態必須為 active 或 block")
    private String status;
}
