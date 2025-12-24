package com.rehome.main.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64; // 必須引入
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rehome.main.entity.CsMessage;
import com.rehome.main.entity.Member;
import com.rehome.main.repository.CsMessageRepo;
import com.rehome.main.repository.MemberRepository;

@RestController
@RequestMapping("/api/cs")
@CrossOrigin(origins = "*")
public class CsChatController {

    @Autowired private CsMessageRepo csMessageRepo;
    @Autowired private MemberRepository memberRepo;
    @Autowired private SimpMessagingTemplate messagingTemplate;

    // --- 1. 取得左側列表 (已強化資料包) ---
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getList(@RequestParam Long myId) {
        List<CsMessage> all = csMessageRepo.findMyAllMessages(myId);
        Map<Long, Map<String, Object>> distinctMap = new LinkedHashMap<>();

        for (CsMessage msg : all) {
            Long otherId = msg.getSenderId().equals(myId) ? msg.getReceiverId() : msg.getSenderId();

            if (!distinctMap.containsKey(otherId)) {
                // 1. 去資料庫找這個人的所有詳細資料
                Member otherMember = memberRepo.findById(otherId).orElse(null);
                
                Map<String, Object> item = new HashMap<>();
                item.put("otherUserId", otherId);
                
                if (otherMember != null) {
                    item.put("otherUserName", otherMember.getName());
                    item.put("otherUserEmail", otherMember.getEmail());
                    item.put("otherUserPhone", otherMember.getPhone());
                    // item.put("otherUserNickname", otherMember.getNick_name());
                    
                    // 2. 處理資料庫中的 icon (mediumblob) 轉成 Base64
                    if (otherMember.getIcon() != null && otherMember.getIcon().length > 0) {
                        String base64Icon = Base64.getEncoder().encodeToString(otherMember.getIcon());
                        item.put("otherUserAvatar", base64Icon);
                    } else {
                        item.put("otherUserAvatar", null);
                    }
                } else {
                    item.put("otherUserName", "用戶 " + otherId);
                }

                item.put("content", (msg.getImg() != null) ? "[圖片]" : msg.getContent());
                // 補上最後一則訊息的時間，讓前端 formatTime 使用
                item.put("sentAt", msg.getSentAt()); 
                
                distinctMap.put(otherId, item);
            }
        }
        return ResponseEntity.ok(new ArrayList<>(distinctMap.values()));
    }

    // --- 2. 取得歷史訊息 (維持不變) ---
    @GetMapping("/history")
    public ResponseEntity<List<CsMessage>> getHistory(@RequestParam Long myId, @RequestParam Long otherId) {
        return ResponseEntity.ok(csMessageRepo.findChatHistory(myId, otherId));
    }

    // --- 3. 發送訊息 (包含圖片) ---
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile image) throws IOException {

        CsMessage msg = CsMessage.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .sentAt(java.time.LocalDateTime.now())
                .build();

        if (image != null && !image.isEmpty()) {
            msg.setImg(image.getBytes());
        }

        CsMessage savedMsg = csMessageRepo.save(msg);

        // 推播給接收者與發送者
        messagingTemplate.convertAndSend("/queue/messages/" + receiverId, savedMsg);
        messagingTemplate.convertAndSend("/queue/messages/" + senderId, savedMsg);
        
        return ResponseEntity.ok(savedMsg);
    }
}