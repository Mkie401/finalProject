package com.rehome.main.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rehome.main.dto.request.ChatListResponseDTO;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.websocket.ChatMessageDTO;
import com.rehome.main.entity.ChatRoom;
import com.rehome.main.entity.Member;
import com.rehome.main.entity.StoreMesg;
import com.rehome.main.repository.ChatRoomRepo;
import com.rehome.main.repository.MemberRepo;
import com.rehome.main.repository.MesgRepo;

@RestController
@RequestMapping("/api/chatroom")
@CrossOrigin(origins = "*") // 開發階段允許跨域
public class MessageController {

    @Autowired
    private ChatRoomRepo chatRoomRepo;
    
    @Autowired
    private MemberRepo memberRepo;
    
    @Autowired
    private MesgRepo mesgRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ==========================================
    // 1. 傳送訊息 (文字 + 圖片) + WebSocket 推播
    // ==========================================
    @PostMapping("/mesg")
    
    public ResponseEntity<ApiResponse<?>> sendMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile image 
            // 注意：這裡變數名改成 image，對應前端 formData.append("image", ...)
    ) {

        // 1. 驗證
        if (senderId == null || receiverId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("必須提供 senderId 與 receiverId"));
        }

        // 2. 找寄件者
        Member sender = memberRepo.findById(senderId)
                .orElseThrow(() -> new RuntimeException("找不到寄件者 ID: " + senderId));

        // 3. 找房間或建立房間
        ChatRoom room = chatRoomRepo.findExistingRoom(senderId, receiverId)
                .orElseGet(() -> {
                    ChatRoom newRoom = ChatRoom.builder()
                            .userAId(senderId)
                            .userBId(receiverId)
                            .roomType(ChatRoom.RoomType.USER_TO_USER)
                            .createDate(LocalDateTime.now())
                            .build();
                    return chatRoomRepo.save(newRoom);
                });

        // 4. 建立並儲存訊息 Entity
        StoreMesg message = new StoreMesg();
        message.setSender(sender);
        message.setChatRoom(room);
        message.setContent(content); 
        message.setSentAt(LocalDateTime.now()); 

        // 處理圖片 (存入 byte[])
        if (image != null && !image.isEmpty()) {
            try {
                message.setImg(image.getBytes()); 
            } catch (IOException e) {
                return ResponseEntity.status(500).body(ApiResponse.fail("圖片上傳失敗"));
            }
        }

        // 存入 DB
        mesgRepo.save(message);

        // 更新房間最後訊息時間
        room.setLastMesgDate(LocalDateTime.now());
        chatRoomRepo.save(room);

        // 5. 轉換成 DTO 再推播
        ChatMessageDTO responseDTO = new ChatMessageDTO(message);

        // 6. WebSocket 推播邏輯 (一對一簡單版)
        
        // // (A) 推給【接收者】 (例如：客戶傳給客服，客服會收到)
        // messagingTemplate.convertAndSendToUser(
        //     String.valueOf(receiverId), 
        //     "/queue/messages",          
        //     responseDTO
        // );
        
        // // (B) 推給【寄件者】 (讓自己畫面也同步跳出剛傳的訊息)
        // messagingTemplate.convertAndSendToUser(
        //      String.valueOf(senderId), 
        //      "/queue/messages",          
        //      responseDTO
        // );

        messagingTemplate.convertAndSend("/topic/messages." + receiverId, responseDTO);
messagingTemplate.convertAndSend("/topic/messages." + senderId, responseDTO);

        return ResponseEntity.ok(ApiResponse.success("成功！訊息 ID: " + message.getId()));
    }

    // ==========================================
    // 2. 取得聊天列表 (左側名單)
    // ==========================================
    @GetMapping("/list")
    public ResponseEntity<List<ChatListResponseDTO>> getChatList(@RequestParam Integer myId) {
        Long userId = myId.longValue();
        
        List<ChatRoom> rooms = chatRoomRepo.findByUserAIdOrUserBId(userId, userId);
        List<ChatListResponseDTO> responseList = new ArrayList<>();

        for (ChatRoom room : rooms) {
            // 判斷對方 ID
            Long otherId = room.getUserAId().equals(userId) ? room.getUserBId() : room.getUserAId();
            
            String name = memberRepo.findById(otherId)
                          .map(Member::getName)
                          .orElse("未知用戶");

            // 抓取最新訊息內容
            String content = "暫無訊息";
            String time = "";

            StoreMesg lastMsg = mesgRepo.findFirstByChatRoom_IdOrderBySentAtDesc(room.getId());

            if (lastMsg != null) {
                if (lastMsg.getContent() != null && !lastMsg.getContent().trim().isEmpty()) {
                    content = lastMsg.getContent();
                    if (content.length() > 20) content = content.substring(0, 20) + "...";
                } else if (lastMsg.getImg() != null && lastMsg.getImg().length > 0) {
                    content = "[圖片]";
                }
                
                if (lastMsg.getSentAt() != null) {
                    time = lastMsg.getSentAt().toString().replace("T", " ").substring(5, 16);
                }
            }

            responseList.add(new ChatListResponseDTO(otherId, name, content, time));
        }

        return ResponseEntity.ok(responseList);
    }

    // ==========================================
    // 3. 取得兩人對話歷史紀錄
    // ==========================================
    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(
            @RequestParam Long myId, 
            @RequestParam Long otherId) {

        ChatRoom room = chatRoomRepo.findExistingRoom(myId, otherId).orElse(null);

        if (room == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<StoreMesg> historyEntities = mesgRepo.findByChatRoom_IdOrderBySentAtAsc(room.getId());
        
        List<ChatMessageDTO> historyDTOs = historyEntities.stream()
                .map(ChatMessageDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(historyDTOs);
    }
    
    // ==========================================
    // 4. 讀取「訊息圖片」的 API
    // ==========================================
    @GetMapping("/message/{id}/img")
    public ResponseEntity<byte[]> getMessageImage(@PathVariable Integer id) {
        StoreMesg mesg = mesgRepo.findById(id).orElse(null);
        if (mesg == null || mesg.getImg() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(mesg.getImg());
    }
}