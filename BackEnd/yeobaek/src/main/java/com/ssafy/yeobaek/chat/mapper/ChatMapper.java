package com.ssafy.yeobaek.chat.mapper;

import com.ssafy.yeobaek.chat.dto.response.ChatRoomData;
import com.ssafy.yeobaek.chat.dto.response.ChatRoomSummary;
import com.ssafy.yeobaek.chat.dto.response.ChatRoomInfo;
import com.ssafy.yeobaek.chat.entity.ChatRoom;
import com.ssafy.yeobaek.chat.entity.Message;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    /**
     * 채팅방 엔티티를 채팅방 정보 DTO로 변환 (새 채팅방)
     * @param chatRoom 채팅방 정보
     * @param partner 상대방 정보
     * @param partnerImageUrl 상대방 프로필 이미지 url
     * @return ChatRoom dto
     */
    public ChatRoomData toChatRoomData(ChatRoom chatRoom, User partner, String partnerImageUrl) {

        ChatRoomSummary chatRoomSummary = new ChatRoomSummary(
                chatRoom.getId(),
                partner.getId(),
                partner.getNickname(),
                partnerImageUrl,
                partner.getUserType()
        );

        return new ChatRoomData(chatRoomSummary);
    }

    /**
     * 채팅방 엔티티를 채팅방 정보 DTO로 변환 (기존 채팅방)
     * @param chatRoom 채팅방 정보
     * @param partner 상대방 정보
     * @param partnerImageUrl 상대방 프로필 이미지 url
     * @param lastMessage 마지막 메시지
     * @return ChatRoomInfo dto
     */
    public ChatRoomInfo toChatRoomInfo(ChatRoom chatRoom, User partner, String partnerImageUrl, Message lastMessage) {
        return ChatRoomInfo.builder()
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