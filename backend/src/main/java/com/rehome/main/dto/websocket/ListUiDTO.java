package com.rehome.main.dto.websocket;

import lombok.Data;

// 這是一個全新的 DTO，專門給列表用，絕對不會影響舊程式
@Data
public class ListUiDTO {
    private Long otherUserId;      // 對方的 ID
    private String otherUserName;  // 對方的名字
    private String lastMessage;    // 預覽文字
    private Boolean hasImage;      // 是不是圖片
    private String iconUrl;        // 對方頭像 (選填)

    public ListUiDTO(Long otherUserId, String otherUserName, String lastMessage, Boolean hasImage) {
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.lastMessage = lastMessage;
        this.hasImage = hasImage;
    }
}