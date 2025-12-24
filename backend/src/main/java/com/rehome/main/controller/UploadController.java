package com.rehome.main.controller;

import com.rehome.main.entity.ChatRoom;
import com.rehome.main.entity.Member;
import com.rehome.main.entity.StoreMesg; // 注意確認你的訊息 Entity 名字
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.repository.ChatRoomRepo;
import com.rehome.main.repository.MemberRepo;
import com.rehome.main.repository.MesgRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UploadController {

    // 建議路徑：為了讓瀏覽器能直接讀取，通常存放在 static/images
    // 請根據你的專案結構調整，或是先存到一個絕對路徑
    private static final String UPLOAD_DIR = "src/main/resources/static/images/"; 

    @Autowired
    private ChatRoomRepo chatRoomRepo;
    @Autowired
    private MemberRepo memberRepo;
    @Autowired
    private MesgRepo mesgRepo;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<?>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("senderId") Long senderId,     // ★ 注意：改成 Long
            @RequestParam("receiverId") Long receiverId  // ★ 注意：改成 Long
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("檔案是空的"));
        }

        try {
            // 1. 處理檔案儲存
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) directory.mkdirs();

            // 產生唯一檔名 (避免重複)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + extension;
            
            Path path = Paths.get(UPLOAD_DIR + newFileName);
            Files.write(path, file.getBytes());

            // 產生的相對路徑 (給前端 src 用)
            // 注意：Spring Boot 需要重啟或設定才能即時讀取新靜態檔案，但存入資料庫的路徑要是對的
            String webPath = "/images/" + newFileName; 

            // 2. 找寄件者
            Member sender = memberRepo.findById(senderId)
                    .orElseThrow(() -> new RuntimeException("找不到寄件者"));

            // 3. ★ 自動找房間 (跟 MessageController 一樣的邏輯)
            ChatRoom room = chatRoomRepo.findExistingRoom(senderId, receiverId)
                    .orElseGet(() -> {
                        ChatRoom newRoom = ChatRoom.builder()
                                .userAId(senderId)
                                .userBId(receiverId)
                                .roomType(ChatRoom.RoomType.USER_TO_USER)
                                .createDate(java.time.LocalDateTime.now())
                                .build();
                        return chatRoomRepo.save(newRoom);
                    });

            // 4. 存入資料庫
            StoreMesg message = new StoreMesg();
            message.setSender(sender);
            message.setChatRoom(room);
            message.setContent(webPath); // ★ 這裡存的是圖片路徑，不是文字！
            
            // message.setMesgType(1); // 如果你有分 0:文字 1:圖片，記得設定

            mesgRepo.save(message);

            // 回傳成功，並把圖片路徑給前端，讓前端可以馬上顯示出來
            return ResponseEntity.ok(ApiResponse.success(webPath));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponse.fail("上傳失敗"));
        }
    }
}