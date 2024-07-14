package com.ywh.yxlmzs.WebSocket;

import com.ywh.yxlmzs.utils.CallApi;

import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
@Component
public class ClientWebSocket extends TextWebSocketHandler {

    private WebSocketSession session;

    private GetGlobalTokenAndPort getGlobalTokenAndPort;

    @Autowired
    public ClientWebSocket(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        CallApi callApi=new CallApi();
        String  port=getGlobalTokenAndPort.getPort();
        String  token=getGlobalTokenAndPort.getToken();
        if (message.getPayload().contains("OnJsonApiEvent_lol-lobby_v2_lobby")){
            if (message.getPayload().contains("Found")) {
                String Url= "/lol-matchmaking/v1/ready-check/accept";
                callApi.callApiPost(Url,token,port,null);
            }
            if (message.getPayload().contains("Searching")){
                System.out.println("Searching for a match");
            }
        }
        System.out.println("Received message: " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 处理连接关闭事件
        System.out.println("Connection closed: " + session.getId());
    }

    public void subscribe(String subscribeMessage) throws Exception {
        // 订阅
        session.sendMessage(new TextMessage(subscribeMessage));
        System.out.println("订阅成功: " + subscribeMessage);
    }

    public void unsubscribe(String unsubscribeMessage) throws Exception {
        //取消订阅
        session.sendMessage(new TextMessage(unsubscribeMessage));
        System.out.println("取消订阅成功: " + unsubscribeMessage);
    }
}
