package com.ssafy.yeobaek.chat.controller;

import com.ssafy.yeobaek.chat.dto.request.ChatMessageRequest;
import com.ssafy.yeobaek.chat.dto.response.ChatMessage;
import com.ssafy.yeobaek.chat.service.ChatService;
import com.ssafy.yeobaek.common.security.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;

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

        if (authentication == null) {
            log.error("[WebSocket] Authentication object not found in session attributes!");
            return;
        }

        JwtUserPrincipal userPrincipal = (JwtUserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.userId();

        log.info("[WebSocket] Message from userId: {}", userId);

        ChatMessage chatMessage = chatService.sendMessage(userId, chatMessageRequest);

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessageRequest.getChatRoomId(), chatMessage);
    }
}
