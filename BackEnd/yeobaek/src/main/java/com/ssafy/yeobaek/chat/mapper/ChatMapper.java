package com.ssafy.yeobaek.chat.mapper;

import com.ssafy.yeobaek.chat.dto.response.ChatRoomSummary;
import com.ssafy.yeobaek.chat.entity.ChatRoom;
import com.ssafy.yeobaek.chat.entity.Message;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    /**
     * 채팅방 엔티티를 채팅방 정보 DTO로 변환 (새 채팅방)
     * @param chatRoom 채팅방 정보
     * @return ChatRoomSummary dto
     */
    public ChatRoomSummary toChatRoomSummary(ChatRoom chatRoom, User partner, String partnerImageUrl) {
        return ChatRoomSummary.builder()
                .chatRoomId(chatRoom.getId())
                .partnerId(partner.getId())
                .partnerName(partner.getNickname())
                .partnerImageUrl(partnerImageUrl)
                .partnerRole(partner.getUserType())
                .build();
    }

    /**
     * 채팅방 엔티티를 채팅방 정보 DTO로 변환 (기존 채팅방)
     * @param chatRoom 채팅방 정보
     * @return ChatRoomSummary dto
     */
    public ChatRoomSummary toChatRoomSummary(ChatRoom chatRoom, User partner, String partnerImageUrl, Message lastMessage) {
        return ChatRoomSummary.builder()
                .chatRoomId(chatRoom.getId())
                .partnerId(partner.getId())
                .partnerName(partner.getNickname())
                .partnerImageUrl(partnerImageUrl)
                .partnerRole(partner.getUserType())
                .unreadCount(1L)
                .lastMessage(lastMessage.getContent())
                .lastMessageAt(lastMessage.getCreatedAt().toString())
                .build();
    }
}