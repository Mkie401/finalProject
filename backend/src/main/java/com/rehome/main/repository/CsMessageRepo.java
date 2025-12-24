package com.rehome.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rehome.main.entity.CsMessage;

public interface CsMessageRepo extends JpaRepository<CsMessage, Long> {

    // 1. 列表用：找出所有跟我有關的訊息 (不管我是寄件者還是收件者)
    @Query("SELECT m FROM CsMessage m WHERE m.senderId = :myId OR m.receiverId = :myId ORDER BY m.sentAt DESC")
    List<CsMessage> findMyAllMessages(@Param("myId") Long myId);

    // 2. 歷史紀錄用：找出我們兩個人之間的對話
    @Query("SELECT m FROM CsMessage m WHERE (m.senderId = :myId AND m.receiverId = :otherId) OR (m.senderId = :otherId AND m.receiverId = :myId) ORDER BY m.sentAt ASC")
    List<CsMessage> findChatHistory(@Param("myId") Long myId, @Param("otherId") Long otherId);
}