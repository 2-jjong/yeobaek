package com.ssafy.yeobaek.chat.service;

import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.artwork.repository.ArtworkRepository;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.repository.CafeRepository;
import com.ssafy.yeobaek.chat.dto.request.CreateChatRoomRequest;
import com.ssafy.yeobaek.chat.dto.response.ChatRoom;
import com.ssafy.yeobaek.chat.entity.ChatRoomMember;
import com.ssafy.yeobaek.chat.repository.ChatRoomMemberRepository;
import com.ssafy.yeobaek.chat.repository.ChatRoomRepository;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;
    private final ArtworkRepository artworkRepository;
    private final CafeRepository cafeRepository;

    /**
     * 채팅방 생성
     * @param senderId 보내는 사람 ID
     * @param request 채팅방 생성 요청
     * @return 생성된 채팅방 정보
     */
    @Override
    @Transactional
    public ChatRoom createChatRoom(Long senderId, CreateChatRoomRequest request) {

        // 1. 요청 유효성 검증
        if ((request.getArtworkId() == null && request.getCafeId() == null) ||
            (request.getArtworkId() != null && request.getCafeId() != null)) {
            throw new CustomException(ResponseCode.CHAT_INVALID_REQUEST);
        }

        // 2. 사용자 조회
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
        User recipient;

        // 3. 상대방 조회
        if (request.getArtworkId() != null) {
            Artwork artwork = artworkRepository.findById(request.getArtworkId())
                    .orElseThrow(() -> new CustomException(ResponseCode.ARTWORK_NOT_FOUND));
            recipient = artwork.getArtist().getUser();
        } else {
            Cafe cafe = cafeRepository.findById(request.getCafeId())
                    .orElseThrow(() -> new CustomException(ResponseCode.CAFE_NOT_FOUND));
            recipient = cafe.getCafeOwner();
        }

        // 4. 기존 1:1 채팅방 확인
        List<ChatRoomMember> senderRooms = chatRoomMemberRepository.findByUser(sender);
        List<ChatRoomMember> recipientRooms = chatRoomMemberRepository.findByUser(recipient);

        Set<com.ssafy.yeobaek.chat.entity.ChatRoom> senderChatRooms = senderRooms.stream()
                .map(ChatRoomMember::getChatRoom)
                .collect(Collectors.toSet());

        Optional<com.ssafy.yeobaek.chat.entity.ChatRoom> existingRoomOpt = recipientRooms.stream()
                .filter(member -> senderChatRooms.contains(member.getChatRoom()))
                .map(ChatRoomMember::getChatRoom)
                .findFirst();

        // 5. 기존 채팅방이 있으면 해당 채팅방 정보 반환
        if (existingRoomOpt.isPresent()) {
//            return chatMapper.toChatRoomDto(existingRoomOpt.get(), senderId);
        }

        // 6. 새로운 채팅방 생성
        com.ssafy.yeobaek.chat.entity.ChatRoom newChatRoom = com.ssafy.yeobaek.chat.entity.ChatRoom.builder().build();
        chatRoomRepository.save(newChatRoom);

        // 7. 채팅방 멤버 생성
        ChatRoomMember senderMember = ChatRoomMember.builder()
                .chatRoom(newChatRoom)
                .user(sender)
                .lastReadAt(LocalDateTime.now())
                .build();

        ChatRoomMember recipientMember = ChatRoomMember.builder()
                .chatRoom(newChatRoom)
                .user(recipient)
                .lastReadAt(LocalDateTime.now())
                .build();

        chatRoomMemberRepository.saveAll(List.of(senderMember, recipientMember));

        newChatRoom.setMembers(List.of(senderMember, recipientMember));

        return ChatRoom.builder()
                .chatRoomId(newChatRoom.getId())
                .build();
    }
}