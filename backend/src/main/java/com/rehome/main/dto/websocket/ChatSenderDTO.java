package com.rehome.main.dto.websocket;

import com.rehome.main.entity.Member; // ★ 記得 import 你的 Entity
import lombok.Data; // ★ 建議加上 Lombok，省去手寫 getter/setter

@Data // 這會自動產生 Getters, Setters, toString
public class ChatSenderDTO {
    
    private Long id;
    private String name;
    private String nickName;
    private String icon; // 頭像 URL (如果是 null 就讓前端顯示預設圖)
    // private String role; // 如果 Member 沒有 role 欄位，這行要拿掉

    // 建構子：把 Member 轉成 DTO
    public ChatSenderDTO(Member member) {
        if (member != null) {
            this.id = member.getId();
            this.name = member.getName();
            this.nickName = member.getNickName();
            
            // 處理頭像：如果是 byte[] 且很大，這裡建議先給 null 或轉成 Base64 (但不建議太長)
            // 假設你的 Member 有 icon 欄位是 String (URL)，就直接給
            // this.icon = member.getIcon(); 
        }
    }
}