package com.rehome.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "store_mesg")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreMesg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // 你的截圖顯示主鍵叫 id (雖然 Excel 寫 mesg_id，但請以資料庫為準)
    private Long id;

    // --- 👇 修正 1：資料庫叫 chatroom_id ---
    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false) 
    private ChatRoom chatRoom;    

    // --- 修正 2：資料庫叫 sender_id (這個原本是對的) ---
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false) 
    private Member sender;        

    // --- 👇 修正 3：資料庫叫 text ---
    @Column(name = "text", length = 1000) 
    private String content; // Java 變數可以維持叫 content，對應 Controller

    @Lob
    @Column(name = "img")
    private byte[] img;

    // --- 👇 修正 4：資料庫叫 time ---
    @CreationTimestamp 
    @Column(name = "time", updatable = false) 
    private LocalDateTime sentAt; // Java 變數叫 sentAt 沒關係
    
    @Column(name = "is_read")
    private Boolean isRead;
}