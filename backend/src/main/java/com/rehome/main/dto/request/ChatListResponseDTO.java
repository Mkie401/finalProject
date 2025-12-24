package com.rehome.main.dto.request;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatListResponseDTO {
    private Long otherUserId;     // 對方的 ID (給 openChat 用)
    private String otherUserName; // 對方的名字 (顯示在列表標題)
    private String lastMessage;   // 最後一句話 (可選)
    private String time;          // 時間 (可選)
}