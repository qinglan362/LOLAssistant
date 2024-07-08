package com.ywh.yxlmzs.service;

import com.ywh.yxlmzs.WebSocket.ClientWebSocket;
import com.ywh.yxlmzs.utils.WebSocketSSL;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.WebSocketHttpHeaders;
import javax.net.ssl.SSLContext;
import java.util.Base64;
@Service
public class WebSocketRegistrationService {

    public void registerWebSocketHandler(String path, String username, String password) throws Exception {
        SSLContext sslContext = WebSocketSSL.createTrustAllSSLContext();

        // 创建WebSocket客户端并设置SSLContext
        StandardWebSocketClient client = new StandardWebSocketClient();
        client.getUserProperties().put("org.apache.tomcat.websocket.SSL_CONTEXT", sslContext);
        WebSocketConnectionManager manager = new WebSocketConnectionManager(client, new ClientWebSocket(), path);

        // 添加Basic Auth认证信息到Header中
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        headers.add("Authorization", authHeader);

        manager.setHeaders(headers);
        manager.start();
    }
}
