package com.ssafy.yeobaek.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "메시지 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Schema(description = "메시지 ID", example = "1")
    private Long messageId;

    @Schema(description = "발신자 유형", example = "ME")
    private Long senderType;

    @Schema(description = "메시지 유형", example = "TEXT")
    private Long messageType;

    @Schema(description = "메시지 내용", example = "안녕하세요")
    private String content;

    @Schema(description = "첨부 이미지 url", example = "https://example.com/image1.jpg")
    private String imageUrl;

    @Schema(description = "전시 정보")
    private String exhibition;

    @Schema(description = "상대방 역할", example = "ARTIST")
    private Boolean isRead;

    @Schema(description = "메시지 전송 일시", example = "2025-11-08 10:56:52")
    private Boolean sentAt;
}
