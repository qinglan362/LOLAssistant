package com.ywh.yxlmzs.service;

import com.ywh.yxlmzs.WebSocket.ClientWebSocket;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import com.ywh.yxlmzs.utils.PickChampionId;
import com.ywh.yxlmzs.utils.WebSocketSSL;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.WebSocketHttpHeaders;
import javax.net.ssl.SSLContext;
import java.util.Base64;

@Service
public class WebSocketRegistrationService {

    private final GetGlobalTokenAndPort getGlobalTokenAndPort;
    private final PickChampionId pickChampionId;

    @Autowired
    public WebSocketRegistrationService(GetGlobalTokenAndPort getGlobalTokenAndPort,PickChampionId pickChampionId) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.pickChampionId=pickChampionId;
    }

    @Getter
    private ClientWebSocket clientWebSocket;

    public void registerWebSocketHandler(String path, String username, String password) throws Exception {
        SSLContext sslContext = WebSocketSSL.createTrustAllSSLContext();

        StandardWebSocketClient client = new StandardWebSocketClient();
        client.getUserProperties().put("org.apache.tomcat.websocket.SSL_CONTEXT", sslContext);
        clientWebSocket = new ClientWebSocket(getGlobalTokenAndPort,pickChampionId);
        WebSocketConnectionManager manager = new WebSocketConnectionManager(client, clientWebSocket, path);

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