package com.rehome.main.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp; // 新增這個引用

import java.time.LocalDateTime;

@Entity
@Table(name = "chatroom")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    /**
     * 聊天室 ID (PK)
     * 資料庫: int unsigned
     * Java: 必須用 Long 才能對應 unsigned 的範圍 (0 ~ 42億)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // ★ 已修正為 Long

    /**
     * 聊天類型
     * Type: ENUM ('USER_TO_USER', 'CS_TO_USER')
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    /**
     * A方 ID (FK) - 送養人 / 會員
     * 資料庫: int unsigned
     */
    @Column(name = "user_a_id", nullable = false)
    private Long userAId; // ★ 已修正為 Long

    /**
     * B方 ID (FK) - 領養人 / 客服(固定ID)
     * 資料庫: int unsigned
     */
    @Column(name = "user_b_id", nullable = false)
    private Long userBId; // ★ 已修正為 Long

    /**
     * 創建時間
     * 加入 @CreationTimestamp 讓 Hibernate 自動幫你填時間
     */
    @CreationTimestamp 
    @Column(name = "create_date", updatable = false)
    private LocalDateTime createDate;

    /**
     * 最後訊息 ID
     * 訊息 ID 也會無限增長，建議也用 Long
     */
    @Column(name = "last_mesg_id")
    private Long lastMesgId; // ★ 已修正為 Long

    /**
     * 最後訊息時間
     */
    @Column(name = "last_mesg_date")
    private LocalDateTime lastMesgDate;

    /**
     * 客服描述 / 備註
     */
    @Column(name = "description", length = 500)
    private String description;

    // --- 定義 Enum ---
    public enum RoomType {
        USER_TO_USER, // 一般使用者對話
        CS_TO_USER    // 客服對使用者
    }
}