package com.rehome.main.dto.websocket;

import com.rehome.main.entity.StoreMesg; // ★ 記得 import 訊息 Entity
import lombok.Data;
import java.util.Base64;

@Data
public class ChatMessageDTO {

    private Long id; // StoreMesg 的 ID 是 Integer 嗎？請確認
    private String content;
    private String img; // 這裡放 Base64 字串或 URL
    private String sentAt; // 傳送時間字串
    private ChatSenderDTO sender; // ★ 這裡包著上面那個瘦身的 SenderDTO

    // 建構子：把 StoreMesg 轉成 DTO
 // 修改建構子，只接收 StoreMesg
    public ChatMessageDTO(StoreMesg mesg) {
        this.id = mesg.getId();
        this.content = mesg.getContent();
        
        // 圖片轉 Base64 邏輯...
        if (mesg.getImg() != null && mesg.getImg().length > 0) {
            this.img = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(mesg.getImg());
        } else {
            this.img = null;
        }

        if (mesg.getSentAt() != null) {
            this.sentAt = mesg.getSentAt().toString();
        }

        // ★ 關鍵修改：直接從 mesg 裡面拿出 sender，不用外面傳進來
        if (mesg.getSender() != null) {
            this.sender = new ChatSenderDTO(mesg.getSender());
        }
    }
    // ★ 這裡才是你原本想寫的 toString (解決 Log 爆炸)
    @Override
    public String toString() {
        // 如果 img 有值且太長，Log 只印出前 20 個字
        String imgPreview = (img != null && img.length() > 20) ? "Base64Image(len=" + img.length() + ")..." : img;
        return "ChatMessageDTO{id=" + id + ", content='" + content + "', img='" + imgPreview + "', sender=" + (sender != null ? sender.getName() : "null") + "}";
    }
}