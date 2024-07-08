package com.ywh.yxlmzs.WebSocket;

import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ClientWebSocket extends TextWebSocketHandler {


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 订阅
        String subscribeMessage = "[5, \"OnJsonApiEvent_lol-lobby_v2_lobby\"]";
        session.sendMessage(new TextMessage(subscribeMessage));
        //取消订阅
        String unsubscribeMessage = "[6, \"OnJsonApiEvent_lol-lobby_v2_lobby\"]";
        session.sendMessage(new TextMessage(unsubscribeMessage));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        CallApi callApi=new CallApi();
        GetGlobalTokenAndPort getGlobalTokenAndPort=new GetGlobalTokenAndPort();

        if (message.getPayload().contains("Found")) {
            String Url= "/lol-matchmaking/v1/ready-check/accept";
            String  port = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
            String  token = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
            callApi.callApiPost(Url,token,port,null);
        }
        if (message.getPayload().contains("Searching")){
            System.out.println("Searching for a match");
        }
        System.out.println("Received message: " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 处理连接关闭事件
        System.out.println("Connection closed: " + session.getId());
    }
}
