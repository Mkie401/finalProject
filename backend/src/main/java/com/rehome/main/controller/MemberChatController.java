package com.rehome.main.controller;

import com.rehome.main.entity.Member;
import com.rehome.main.repository.MemberRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member") // 設定統一入口
@CrossOrigin(origins = "*")    // 允許前端跨網域存取
public class MemberChatController {

    @Autowired
    private MemberRepo memberRepo;

    // ==========================================
    // 取得使用者頭像 API
    // 用法: <img src="http://localhost:8081/api/member/icon/1">
    // ==========================================
    @GetMapping("/icon/{id}")
    public ResponseEntity<byte[]> getMemberIcon(@PathVariable Long id) {
        // 1. 去資料庫查這個 ID 的會員
        Member member = memberRepo.findById(id).orElse(null);

        // 2. 如果找不到人，或是該欄位是 NULL (像你截圖中的 ID:2)
        if (member == null || member.getIcon() == null) {
            return ResponseEntity.notFound().build(); // 回傳 404
        }

        // 3. 如果有圖，回傳圖片檔案流
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // 告訴瀏覽器這是圖片
                .body(member.getIcon());
    }
}