package com.rehome.main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ★★★ 這裡才是解決「無法交握」的關鍵 ★★★
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // 1. 允許跨域 (解決 403 Forbidden)
                .withSockJS();                 // 2. 啟用 SockJS (對應前端 new SockJS)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 1. 設定訊息代理 (Broker)
        // 只要 "/topic" (廣播) 和 "/queue" (點對點轉換後的目標)
        registry.enableSimpleBroker("/topic", "/queue","/user"); 
        
        // 2. 設定前端發送訊息的前綴
        // 前端呼叫 stompClient.send("/app/chat", ...) 時使用
        registry.setApplicationDestinationPrefixes("/app");
        
        // 3. 設定點對點使用者的前綴 (預設就是 /user，這行其實可以不寫，但寫了清楚)
        // 前端訂閱 stompClient.subscribe("/user/queue/messages", ...) 時使用
        registry.setUserDestinationPrefix("/user");
    }
}