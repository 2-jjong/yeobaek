package com.ssafy.yeobaek.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "채팅방 기본 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {
    @Schema(description = "채팅방 ID", example = "1")
    private Long chatRoomId;

    @Schema(description = "상대방 ID", example = "1")
    private Long partnerId;

    @Schema(description = "상대방 이름 (작가명 또는 카페명)", example = "홍길동")
    private String partnerName;

    @Schema(description = "상대방 ID", example = "1")
    private String partnerRole;

    @Schema(description = "카페 ID", example = "1")
    private Long cafeId;

    @Schema(description = "카페명", example = "Brew & Canvas")
    private String cafeName;

    @Schema(description = "작품 ID", example = "1")
    private Long artworkId;

    @Schema(description = "작품명", example = "기억의 조각")
    private String artworkTitle;

    @Schema(description = "작품 썸네일 url", example = "https://example.com/artwork1.jpg")
    private String artworkThumbnailUrl;

    @Schema(description = "읽지 않은 메시지 수", example = "3")
    private Long unreadCount;

    @Schema(description = "마지막 메시지 내용", example = "안녕하세요")
    private String lastMessage;

    @Schema(description = "마지막 메시지 일시", example = "2025-11-06 02:07:41")
    private String lastMessageAt;
}
