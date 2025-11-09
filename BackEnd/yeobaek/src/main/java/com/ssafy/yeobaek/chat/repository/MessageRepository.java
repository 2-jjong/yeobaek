package com.ssafy.yeobaek.chat.repository;

import com.ssafy.yeobaek.chat.entity.ChatRoom;
import com.ssafy.yeobaek.chat.entity.Message;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);
    Page<Message> findByChatRoom(ChatRoom chatRoom, Pageable pageable);

    Optional<Message> findTopByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);
    Long countByChatRoomAndUserNotAndIsReadFalse(ChatRoom chatRoom, User user);
}
