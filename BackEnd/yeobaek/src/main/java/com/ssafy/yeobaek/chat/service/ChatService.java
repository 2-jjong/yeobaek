package com.ssafy.yeobaek.chat.service;

import com.ssafy.yeobaek.chat.dto.request.CreateChatRoomRequest;
import com.ssafy.yeobaek.chat.dto.response.ChatRoom;

public interface ChatService {

    /**
     * 채팅방 생성
     * @param senderId 보내는 사람 ID
     * @param request 채팅방 생성 요청 (수신자 ID 포함)
     * @return 생성된 채팅방 정보
     */
    ChatRoom createChatRoom(Long senderId, CreateChatRoomRequest request);
}
