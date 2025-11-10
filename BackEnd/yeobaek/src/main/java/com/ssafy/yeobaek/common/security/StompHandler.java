package com.ssafy.yeobaek.common.security;

import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.common.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.info("STOMP CONNECT - Authorization header found.");

            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                String token = jwtToken.substring(7);
                try {
                    if (jwtUtil.validateToken(token)) {
                        Long userId = jwtUtil.getUserIdFromToken(token);
                        UserRole userType = jwtUtil.getUserTypeFromToken(token);

                        JwtUserPrincipal principal = new JwtUserPrincipal(userId, userType);
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userType.name()))
                        );
                        // 세션 속성에 인증 정보 저장
                        accessor.getSessionAttributes().put("userAuthentication", authentication);
                        accessor.setUser(authentication);
                        log.info("STOMP User authenticated and session attribute set: userId={}", userId);
                    }
                } catch (Exception e) {
                    log.warn("STOMP connection failed: Invalid JWT token - {}", e.getMessage());
                    throw new IllegalArgumentException("Invalid JWT token");
                }
            } else {
                log.warn("STOMP connection failed: Missing or invalid Authorization header");
                throw new IllegalArgumentException("Missing or invalid Authorization header");
            }
        } else if (StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command)) {
            // CONNECT 이후의 다른 요청에서 세션에 저장된 인증 정보 사용
            Authentication authentication = (Authentication) accessor.getSessionAttributes().get("userAuthentication");
            if (authentication != null) {
                accessor.setUser(authentication);
            }
        }
        return message;
    }
}
