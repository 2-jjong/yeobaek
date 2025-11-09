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

        // STOMP CONNECT 요청일 때 JWT 인증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.info("STOMP CONNECT - Authorization Header: {}", jwtToken);

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
                        accessor.setUser(authentication);
                        log.info("STOMP User authenticated: userId={}, userType={}", userId, userType);
                    } else {
                        log.warn("STOMP connection failed: Invalid JWT token");
                        // 유효하지 않은 토큰일 경우 연결 거부 (또는 예외 처리)
                        throw new IllegalArgumentException("Invalid JWT token");
                    }
                } catch (ExpiredJwtException e) {
                    log.warn("STOMP connection failed: Expired JWT token");
                    throw new IllegalArgumentException("Expired JWT token");
                } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
                    log.warn("STOMP connection failed: Invalid JWT token - {}", e.getMessage());
                    throw new IllegalArgumentException("Invalid JWT token");
                }
            } else {
                log.warn("STOMP connection failed: Missing or invalid Authorization header");
                // 토큰이 없거나 형식이 잘못된 경우 연결 거부
                throw new IllegalArgumentException("Missing or invalid Authorization header");
            }
        }
        return message;
    }
}
