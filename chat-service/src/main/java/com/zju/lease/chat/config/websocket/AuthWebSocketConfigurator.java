package com.zju.lease.chat.config.websocket;

import com.zju.lease.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class AuthWebSocketConfigurator extends ServerEndpointConfig.Configurator {

    private static final Logger log = LoggerFactory.getLogger(AuthWebSocketConfigurator.class);

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        Map<String, List<String>> parameterMap = request.getParameterMap();
        List<String> tokenList = parameterMap.get("token");

        if (tokenList != null && !tokenList.isEmpty()) {
            String token = tokenList.get(0);
            try {
                Claims claims = JwtUtil.parseToken(token);
                String username = claims.get("username", String.class);
                Long userId = claims.get("userId", Long.class);

                sec.getUserProperties().put("username", username);
                sec.getUserProperties().put("userId", userId);
                log.info("WebSocket auth success, userId: {}, username: {}", userId, username);
            } catch (Exception e) {
                log.error("WebSocket auth failed: {}", e.getMessage());
                // Don't throw here - let it proceed to onOpen which will close properly
                sec.getUserProperties().put("username", null);
                sec.getUserProperties().put("userId", null);
            }
        } else {
            log.error("WebSocket auth failed: token is empty");
            sec.getUserProperties().put("username", null);
            sec.getUserProperties().put("userId", null);
        }
    }
}
