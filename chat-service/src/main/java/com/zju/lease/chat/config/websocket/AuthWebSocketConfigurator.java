package com.zju.lease.chat.config.websocket;

import com.zju.lease.common.exception.LeaseException;
import com.zju.lease.common.result.ResultCodeEnum;
import com.zju.lease.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

import java.util.List;
import java.util.Map;

public class AuthWebSocketConfigurator extends ServerEndpointConfig.Configurator {

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
            } catch (Exception e) {
                throw new LeaseException(ResultCodeEnum.TOKEN_INVALID);
            }
        } else {
            throw new LeaseException(ResultCodeEnum.TOKEN_EXPIRED);
        }
    }
}
