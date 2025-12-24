package com.rehome.main.repository;

import com.rehome.main.entity.StoreMesg;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // ★ 1. 記得要 Import List

// 注意：你的 StoreMesg ID 是 Integer，所以這裡寫 <StoreMesg, Integer>
public interface MesgRepo extends JpaRepository<StoreMesg, Integer> {


    // ChatRoom (對應 chatRoom 變數) + _Id (對應 ID)
    // SentAt (對應 sentAt 變數)
    List<StoreMesg> findByChatRoom_IdOrderBySentAtAsc(Long chatRoomId);
    StoreMesg findFirstByChatRoom_IdOrderBySentAtDesc(Long chatRoomId);
}