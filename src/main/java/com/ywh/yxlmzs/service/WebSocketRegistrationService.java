package com.ywh.yxlmzs.service;

import com.ywh.yxlmzs.WebSocket.ClientWebSocket;
import com.ywh.yxlmzs.utils.*;
import jakarta.websocket.WebSocketContainer;
import lombok.Getter;
import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.WebSocketHttpHeaders;
import javax.net.ssl.SSLContext;
import java.util.Base64;

@Service
public class WebSocketRegistrationService {

    private GetGlobalTokenAndPort getGlobalTokenAndPort;
    private PickChampionId pickChampionId;
    private BanChampionId banChampionId;
    private AutoContinueNextGame autoContinueNextGame;
    private AutoAccecptMatch autoAccecptMatch;
    private AutoSearchMatch autoSearchMatch;

    @Autowired
    public WebSocketRegistrationService(GetGlobalTokenAndPort getGlobalTokenAndPort,
                                        PickChampionId pickChampionId,
                                        BanChampionId banChampionId,
                                        AutoContinueNextGame autoContinueNextGame,
                                        AutoAccecptMatch autoAccecptMatch,
                                        AutoSearchMatch autoSearchMatch
                                        ) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.pickChampionId=pickChampionId;
        this.banChampionId=banChampionId;
        this.autoContinueNextGame=autoContinueNextGame;
        this.autoAccecptMatch=autoAccecptMatch;
        this.autoSearchMatch=autoSearchMatch;
    }

    @Getter
    private ClientWebSocket clientWebSocket;

    public void registerWebSocketHandler(String path, String username, String password) throws Exception {
        SSLContext sslContext = WebSocketSSL.createTrustAllSSLContext();

        WebSocketContainer container = new WsWebSocketContainer();
// 设置二进制消息缓冲区大小（以字节为单位）
        container.setDefaultMaxBinaryMessageBufferSize(5120000);
// 设置文本消息缓冲区大小（以字节为单位）
        container.setDefaultMaxTextMessageBufferSize(5120000);
// 设置会话空闲超时时间（以毫秒为单位）设置为24小时
        container.setDefaultMaxSessionIdleTimeout(86400000);

        StandardWebSocketClient client = new StandardWebSocketClient(container);
        client.getUserProperties().put("org.apache.tomcat.websocket.SSL_CONTEXT", sslContext);


        clientWebSocket = new ClientWebSocket(getGlobalTokenAndPort,pickChampionId,banChampionId,autoContinueNextGame,autoAccecptMatch,autoSearchMatch);
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