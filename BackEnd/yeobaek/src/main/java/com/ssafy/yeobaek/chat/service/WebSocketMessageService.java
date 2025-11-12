package com.ssafy.yeobaek.chat.service;

import com.ssafy.yeobaek.chat.dto.response.ChatMessage;
import com.ssafy.yeobaek.chat.dto.response.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendChatMessage(Long chatRoomId, ChatMessage chatMessage) {
        log.info("[WebSocketMessageSenderService] Sending message to WebSocket for chatRoomId: {}", chatRoomId);
        String destination = "/sub/chat/room/" + chatRoomId;
        messagingTemplate.convertAndSend(destination, chatMessage);
        log.info("[WebSocketMessageSenderService] Message sent to destination: {}", destination);
    }

    public void sendEventToUser(String username, EventDto eventDto) {
        log.info("[WebSocketMessageSenderService] Sending event to user: {} with type: {}", username, eventDto.getEventType());
        String destination = "/queue/notifications";
        messagingTemplate.convertAndSendToUser(username, destination, eventDto);
        log.info("[WebSocketMessageSenderService] Event sent to user: {} on destination: {}", username, destination);
    }
}
