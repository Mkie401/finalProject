package com.rehome.main.component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class OnlineStatusManager {
    // 使用執行緒安全的 Set 存儲在線使用者的 ID
    private final Set<Long> onlineUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void add(Long userId) { onlineUsers.add(userId); }
    public void remove(Long userId) { onlineUsers.remove(userId); }
    public boolean isOnline(Long userId) { return onlineUsers.contains(userId); }
    public Set<Long> getAllOnline() { return onlineUsers; }
}