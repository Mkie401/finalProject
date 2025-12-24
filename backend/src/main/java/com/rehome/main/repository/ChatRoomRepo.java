package com.rehome.main.repository;

import com.rehome.main.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface ChatRoomRepo extends JpaRepository<ChatRoom, Integer> {

    // ★ 新增這段：不管 A 和 B 順序為何，只要這兩個人在同一個房間，就抓出來
    @Query("SELECT c FROM ChatRoom c WHERE " +
           "(c.userAId = :userId1 AND c.userBId = :userId2) OR " +
           "(c.userAId = :userId2 AND c.userBId = :userId1)")
    Optional<ChatRoom> findExistingRoom(@Param("userId1") Long userId1, @Param("userId2")  Long userId2);
    List<ChatRoom> findByUserAIdOrUserBId(Long userAId, Long userBId);
}