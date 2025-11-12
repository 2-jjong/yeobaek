package com.ssafy.yeobaek.chat.mapper;

import com.ssafy.yeobaek.chat.dto.request.ChatMessageRequest;
import com.ssafy.yeobaek.chat.dto.response.ChatMessage;
import com.ssafy.yeobaek.chat.dto.response.ChatRoomData;
import com.ssafy.yeobaek.chat.dto.response.ChatRoomSummary;
import com.ssafy.yeobaek.chat.dto.response.ChatRoomInfo;
import com.ssafy.yeobaek.chat.entity.ChatRoom;
import com.ssafy.yeobaek.chat.entity.Message;
import com.ssafy.yeobaek.common.enums.MessageType;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ChatMapper {

    /**
     * 채팅방 엔티티를 채팅방 정보 DTO로 변환 (새 채팅방)
     * @param chatRoom 채팅방 정보
     * @param partner 상대방 정보
     * @param partnerImageUrl 상대방 프로필 이미지 url
     * @return ChatRoomData dto
     */
    public ChatRoomData toChatRoomData(ChatRoom chatRoom, User partner, String partnerImageUrl) {
        return new ChatRoomData(toChatRoomSummary(chatRoom, partner, partnerImageUrl));
    }

    /**
     * 채팅방 엔티티와 파트너 정보를 ChatRoomSummary DTO로 변환
     * @param chatRoom 채팅방 엔티티
     * @param partner 상대방 User 엔티티
     * @param partnerImageUrl 상대방 프로필 이미지 URL
     * @return ChatRoomSummary DTO
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
     * 채팅방 목록의 각 항목 DTO로 변환
     * @param chatRoom 채팅방 엔티티
     * @param partner 상대방 User 엔티티
     * @param partnerImageUrl 상대방 프로필 이미지 URL
     * @param lastMessageOpt 마지막 메시지
     * @param unreadCount 읽지 않은 메시지 수
     * @return ChatRoomInfo DTO
     */
    public ChatRoomInfo toChatRoomInfo(ChatRoom chatRoom, User partner, String partnerImageUrl, Optional<Message> lastMessageOpt, Long unreadCount) {
        return ChatRoomInfo.builder()
                .chatRoomId(chatRoom.getId())
                .partnerId(partner.getId())
                .partnerName(partner.getNickname())
                .partnerImageUrl(partnerImageUrl)
                .partnerRole(partner.getUserType())
                .unreadCount(unreadCount)
                .lastMessage(lastMessageOpt.map(Message::getContent).orElse(null))
                .lastMessageAt(lastMessageOpt.map(Message::getCreatedAt).orElse(null))
                .lastMessageType(lastMessageOpt.map(Message::getMessageType).orElse(null))
                .build();
    }

    /**
     * Message 엔티티를 ChatMessage DTO로 변환
     * @param message Message 엔티티
     * @return ChatMessage DTO
     */
    public ChatMessage toChatMessage(Message message) {

        return ChatMessage.builder()
                .messageId(message.getId())
                .senderId(message.getUser() != null ? message.getUser().getId() : null)
                .messageType(message.getMessageType())
                .content(message.getContent())
                .imageUrl(message.getImageUrl())
                .exhibitionId(message.getExhibitionId())
                .exhibition(null) // TODO: 전시 정보 연동 필요
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
    }

    /**
     * Message 엔티티 목록을 ChatMessage DTO 목록으로 변환
     * @param messages Message 엔티티 목록
     * @param currentUserId 현재 사용자 ID
     * @return ChatMessage DTO 목록
     */
    public List<ChatMessage> toChatMessageList(List<Message> messages, Long currentUserId) {
        return messages.stream()
                .map(this::toChatMessage)
                .collect(Collectors.toList());
    }

    /**
     * 메시지 전송 요청 정보를 Message 엔티티로 변환
     * @param chatRoom 채팅방 정보
     * @param sender 발신자 정보
     * @param request 메시지 전송 요청 정보
     * @return Message 엔티티
     */
    public Message toMessageEntity(ChatRoom chatRoom, User sender, ChatMessageRequest request) {
        return Message.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .messageType(request.getMessageType())
                .build();
    }
}