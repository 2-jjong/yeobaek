package com.ssafy.yeobaek.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.yeobaek.chat.dto.response.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 메시지가 발행되면 대기하고 있던 RedisSubscriber가 해당 메시지를 받아 처리합니다.
     */
    public void sendMessage(Long chatRoomId, String publishMessage) {
        try {
            // ChatMessage 객체로 맵핑
            ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
            // WebSocket 구독자에게 메시지 전송
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoomId, chatMessage);
        } catch (Exception e) {
            log.error("Exception {}", e.getMessage());
        }
    }
}
