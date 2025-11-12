package com.ssafy.yeobaek.chat.controller;

import com.ssafy.yeobaek.chat.dto.request.ChatMessageRequest;
import com.ssafy.yeobaek.chat.dto.response.ChatMessage;
import com.ssafy.yeobaek.chat.service.ChatService;
import com.ssafy.yeobaek.common.security.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final ChatService chatService;

    @MessageMapping("/messages")
    public void sendMessage(
            StompHeaderAccessor accessor,
            @Payload ChatMessageRequest chatMessageRequest
    ) {
        log.info("[WebSocket] Received message: {}", chatMessageRequest.getContent());

        // StompHeaderAccessor에서 세션 속성을 직접 꺼내 사용
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) {
            log.error("[WebSocket] Session attributes are null!");
            return;
        }

        Authentication authentication = (Authentication) sessionAttributes.get("userAuthentication");

        if (authentication == null || authentication.getPrincipal() == null) {
            log.error("[WebSocket] Authentication object not found or principal is null in session attributes!");
            return;
        }

        // 1. 서버가 STOMP 세션에서 인식한 사용자 ID 추출
        JwtUserPrincipal userPrincipal = (JwtUserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.userId();

        try {
            // 2. 메시지 처리 로직 수행
            log.info("[WebSocket] Calling chatService.sendMessage for userId: {}", userId);
            chatService.sendMessage(userId, chatMessageRequest);

        } catch (Exception e) {
            log.error("[WebSocket] Error during message processing for userId: {}", userId, e);
        }
    }
}