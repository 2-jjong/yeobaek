package com.ssafy.yeobaek.chat.controller;

import com.ssafy.yeobaek.chat.dto.request.CreateChatRoomRequest;
import com.ssafy.yeobaek.chat.dto.response.ChatRoomSummary;
import com.ssafy.yeobaek.chat.service.ChatService;
import com.ssafy.yeobaek.common.dto.ResponseDto;
import com.ssafy.yeobaek.common.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "채팅", description = "채팅 관련 API")
@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "채팅방 생성", description = "채팅방을 생성합니다. 이미 채팅방이 존재하는 경우 기존 채팅방을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"message\": \"정상 처리\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "수신자 오류", value = "{\"code\": \"MS4001\", \"message\": \"자신에게 메시지를 보낼 수 없습니다\"}"),
                                    @ExampleObject(name = "필수 값 누락", value = "{\"code\": \"S4001\", \"message\": \"필수 입력값이 누락되었습니다\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 토큰", value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"),
                                    @ExampleObject(name = "만료된 토큰", value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "사용자를 찾을 수 없음", value = "{\"code\": \"US4041\", \"message\": \"존재하지 않는 회원입니다\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @PostMapping()
    public ResponseEntity<ResponseDto<ChatRoomSummary>> createChatRoom(@Valid @RequestBody CreateChatRoomRequest request) {
        Long senderId = SecurityUtil.getCurrentUserId();
        ChatRoomSummary chatRoomSummary = chatService.createChatRoom(senderId, request.getPartnerId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.created(chatRoomSummary));
    }
}
