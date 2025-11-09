package com.ssafy.yeobaek.chat.service;

import com.ssafy.yeobaek.chat.dto.request.ChatMessageRequest;
import com.ssafy.yeobaek.chat.dto.response.ChatMessage;
import com.ssafy.yeobaek.chat.dto.response.ChatRoomData;
import com.ssafy.yeobaek.chat.dto.response.ChatRoomDetailData;

public interface ChatService {

    /**
     * 채팅방 생성
     * @param userId 사용자 ID
     * @param partnerId 상대방 ID
     * @return 생성된 채팅방 정보
     */
    ChatRoomData createChatRoom(Long userId, Long partnerId);

    /**
     * 특정 채팅방 메시지 조회
     * @param userId 사용자 ID
     * @param chatRoomId 채팅방 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 채팅방 정보
     */
    ChatRoomDetailData getChatRoomMessages(Long userId, Long chatRoomId, Integer page, Integer size);

    /**
     * 채팅 메시지 전송
     * @param userId 발신자 ID
     * @param chatMessageRequest 채팅 메시지 요청
     * @return 전송된 채팅 메시지 정보
     */
    ChatMessage sendMessage(Long userId, ChatMessageRequest chatMessageRequest);
}
