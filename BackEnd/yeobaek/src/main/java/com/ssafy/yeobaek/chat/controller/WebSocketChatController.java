package com.ssafy.yeobaek.chat.controller;

import com.ssafy.yeobaek.chat.dto.request.ChatMessageRequest;
import com.ssafy.yeobaek.chat.dto.response.ChatMessage;
import com.ssafy.yeobaek.chat.service.ChatService;
import com.ssafy.yeobaek.common.security.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final ChatService chatService;

    @MessageMapping("/messages")
    public ChatMessage sendMessage(
            @Payload ChatMessageRequest chatMessageRequest,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal.userId();
        return chatService.sendMessage(userId, chatMessageRequest);
    }
}
