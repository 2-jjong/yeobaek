package com.ssafy.yeobaek.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.yeobaek.chat.dto.request.ChatMessageRequest;
import com.ssafy.yeobaek.chat.dto.response.ChatMessage;
import com.ssafy.yeobaek.chat.dto.response.ChatRoomData;
import com.ssafy.yeobaek.chat.dto.response.ChatRoomDetailData;
import com.ssafy.yeobaek.chat.dto.response.ChatRoomSummary;
import com.ssafy.yeobaek.chat.entity.ChatRoom;
import com.ssafy.yeobaek.chat.entity.ChatRoomMember;
import com.ssafy.yeobaek.chat.entity.Message;
import com.ssafy.yeobaek.chat.mapper.ChatMapper;
import com.ssafy.yeobaek.chat.repository.ChatRoomMemberRepository;
import com.ssafy.yeobaek.chat.repository.ChatRoomRepository;
import com.ssafy.yeobaek.chat.repository.MessageRepository;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.dto.Pagination;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.storage.service.S3Service;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final StringRedisTemplate stringRedisTemplate;
    private final ChannelTopic channelTopic;
    private final ObjectMapper objectMapper;

    /**
     * 채팅방 생성
     * @param userId 사용자 ID
     * @param partnerId 상대방 ID
     * @return 생성된 채팅방 정보
     */
    @Override
    @Transactional
    public ChatRoomData createChatRoom(Long userId, Long partnerId) {

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
            return chatMapper.toChatRoomData(existingRoomOpt.get(), partner, partnerImageUrl);
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

        return chatMapper.toChatRoomData(newChatRoom, partner, partnerImageUrl);
    }

    /**
     * 특정 채팅방 메시지 조회
     * @param userId 사용자 ID
     * @param chatRoomId 채팅방 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 채팅방 정보
     */
    @Override
    public ChatRoomDetailData getChatRoomMessages(Long userId, Long chatRoomId, Integer page, Integer size) {

        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ResponseCode.MESSAGE_ROOM_NOT_FOUND));

        // 2. 사용자 권한 확인 (채팅방 멤버인지 확인)
        chatRoomMemberRepository.findByChatRoomAndUser_Id(chatRoom, userId)
                .orElseThrow(() -> new CustomException(ResponseCode.MESSAGE_ROOM_FORBIDDEN));

        // 3. 상대방 정보 조회
        User partner = chatRoom.getMembers().stream()
                .map(ChatRoomMember::getUser)
                .filter(user -> !user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        String partnerImageUrl = s3Service.generatePresignedUrlForDownload(partner.getProfileImageUrl());

        // 4. 채팅방 요약 정보 생성
        ChatRoomSummary chatRoomSummary = chatMapper.toChatRoomSummary(chatRoom, partner, partnerImageUrl);

        // 5. 메시지 목록 조회 (페이지네이션 적용)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Message> messagePage = messageRepository.findByChatRoom(chatRoom, pageable);
        List<ChatMessage> chatMessages = chatMapper.toChatMessageList(messagePage.getContent(), userId);

        // 6. 페이징 정보 생성
        Pagination pagination = new Pagination(
                messagePage.getNumber(),
                messagePage.getSize(),
                messagePage.getTotalElements(),
                messagePage.getTotalPages(),
                messagePage.isLast()
        );

        return new ChatRoomDetailData(chatRoomSummary, chatMessages, pagination);
    }

    /**
     * 채팅 메시지 전송
     * @param userId 발신자 ID
     * @param request 채팅 메시지 요청
     * @return 전송된 채팅 메시지 정보
     */
    @Override
    @Transactional
    public ChatMessage sendMessage(Long userId, ChatMessageRequest request) {

        // 1. 채팅방 유효성 검증
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new CustomException(ResponseCode.MESSAGE_ROOM_NOT_FOUND));

        // 2. 발신자 유효성 검증
        User sender = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        // 3. 사용자 권한 확인 (채팅방 멤버인지 확인)
        chatRoomMemberRepository.findByChatRoomAndUser_Id(chatRoom, userId)
                .orElseThrow(() -> new CustomException(ResponseCode.MESSAGE_ROOM_FORBIDDEN));

        // 4. 메시지 엔티티 생성 및 저장
        Message message = chatMapper.toMessageEntity(chatRoom, sender, request);
        messageRepository.save(message);

        // 5. ChatMessage 생성
        ChatMessage chatMessage = chatMapper.toChatMessage(message, userId);

        // 6. Redis Pub/Sub을 통해 메시지 발행
        try {
            stringRedisTemplate.convertAndSend(channelTopic.getTopic(), objectMapper.writeValueAsString(chatMessage));
        } catch (Exception e) {
            throw new CustomException(ResponseCode.MESSAGE_SEND_FAILED);
        }

        return chatMessage;
    }
}