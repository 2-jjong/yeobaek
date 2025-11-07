package com.ssafy.yeobaek.chat.service;

import com.ssafy.yeobaek.chat.dto.response.ChatRoomSummary;
import com.ssafy.yeobaek.chat.entity.ChatRoom;
import com.ssafy.yeobaek.chat.entity.ChatRoomMember;
import com.ssafy.yeobaek.chat.mapper.ChatMapper;
import com.ssafy.yeobaek.chat.repository.ChatRoomMemberRepository;
import com.ssafy.yeobaek.chat.repository.ChatRoomRepository;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.storage.service.S3Service;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMapper chatMapper;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserService userService;
    private final S3Service s3Service;

    /**
     * 채팅방 생성
     * @param userId 사용자 ID
     * @param partnerId 상대방 ID
     * @return 생성된 채팅방 정보
     */
    @Override
    @Transactional
    public ChatRoomSummary createChatRoom(Long userId, Long partnerId) {

        // 1. 유효성 검증
        if (userId.equals(partnerId)) throw new CustomException(ResponseCode.MESSAGE_SELF_SEND_NOT_ALLOWED);

        // 2. 사용자 조회
        User user = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));
        User partner = userService.findById(partnerId)
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        // 3. 기존 1:1 채팅방 확인
        List<ChatRoomMember> senderRooms = chatRoomMemberRepository.findByUser(user);
        List<ChatRoomMember> recipientRooms = chatRoomMemberRepository.findByUser(partner);

        Set<ChatRoom> senderChatRooms = senderRooms.stream()
                .map(ChatRoomMember::getChatRoom)
                .collect(Collectors.toSet());

        Optional<ChatRoom> existingRoomOpt = recipientRooms.stream()
                .filter(member -> senderChatRooms.contains(member.getChatRoom()))
                .map(ChatRoomMember::getChatRoom)
                .findFirst();

        // 4. 기존 채팅방이 있으면 해당 채팅방 정보 반환
        if (existingRoomOpt.isPresent()) {
            String partnerImageUrl = s3Service.generatePresignedUrlForDownload(partner.getProfileImageUrl());
            return chatMapper.toChatRoomSummary(existingRoomOpt.get(), partner, partnerImageUrl);
        }

        // 5. 새로운 채팅방 생성
        ChatRoom newChatRoom = ChatRoom.builder().build();
        chatRoomRepository.save(newChatRoom);

        // 6. 채팅방 멤버 생성
        ChatRoomMember senderMember = ChatRoomMember.builder()
                .chatRoom(newChatRoom)
                .user(user)
                .build();

        ChatRoomMember recipientMember = ChatRoomMember.builder()
                .chatRoom(newChatRoom)
                .user(partner)
                .build();

        chatRoomMemberRepository.saveAll(List.of(senderMember, recipientMember));

        newChatRoom.setMembers(List.of(senderMember, recipientMember));

        // 7. 프로필 이미지 Url 발급
        String partnerImageUrl = s3Service.generatePresignedUrlForDownload(partner.getProfileImageUrl());

        return chatMapper.toChatRoomSummary(newChatRoom, partner, partnerImageUrl);
    }
}