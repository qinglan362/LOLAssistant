package com.ywh.yxlmzs.WebSocket;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.utils.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Objects;

@Component
public class ClientWebSocket extends TextWebSocketHandler {


    private WebSocketSession session;
    private GetGlobalTokenAndPort getGlobalTokenAndPort;
    private PickChampionId pickChampionId;
    private BanChampionId banChampionId;
    private AutoContinueNextGame autoContinueNextGame;
    private AutoAccecptMatch autoAccecptMatch;
    private AutoSearchMatch autoSearchMatch;
    private String state;
    private AutoSwap autoSwap;

    @Autowired
    public ClientWebSocket(GetGlobalTokenAndPort getGlobalTokenAndPort,
                           PickChampionId pickChampionId,
                           BanChampionId banChampionId,
                           AutoContinueNextGame autoContinueNextGame,
                           AutoAccecptMatch autoAccecptMatch,
                           AutoSearchMatch autoSearchMatch,
                           AutoSwap autoSwap
                           ) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.pickChampionId=pickChampionId;
        this.banChampionId=banChampionId;
        this.autoContinueNextGame=autoContinueNextGame;
        this.autoAccecptMatch=autoAccecptMatch;
        this.autoSearchMatch=autoSearchMatch;
        this.autoSwap=autoSwap;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
        session.sendMessage(new TextMessage("[5, \"OnJsonApiEvent_lol-champ-select_v1_session\"]"));
        session.sendMessage(new TextMessage("[5, \"OnJsonApiEvent_lol-gameflow_v1_gameflow-phase\"]"));
    }
    ///lol-champ-select/v1/session/trades/{id}/accept
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        CallApi callApi=new CallApi();
        ObjectMapper objectMapper = new ObjectMapper();
        String  port=getGlobalTokenAndPort.getPort();
        String  token=getGlobalTokenAndPort.getToken();



       if (message.getPayload().contains("/lol-gameflow/v1/gameflow-phase")) {

           JSONArray jsonArray = JSONArray.parseArray(message.getPayload());
           JSONObject jsonObject = jsonArray.getJSONObject(2);
           state=jsonObject.getString("data");
           System.out.println("当前状态:"+state);
           if (message.getPayload().contains("Lobby")) {
               if (!Objects.isNull(autoSearchMatch)&&!Objects.isNull(autoSearchMatch.getState())&&!autoSearchMatch.getState().isEmpty()&&autoSearchMatch.getState().equals("true")){
                   while (true){
                       if (!objectMapper.readTree(callApi.callApiGet("/lol-gameflow/v1/gameflow-phase",
                               token,
                               port,
                               null)).asText().equals("Lobby")){
                           break;
                       }
                       JsonNode jsonNode=objectMapper.readTree(callApi.callApiGet("/lol-lobby/v2/lobby", token, port, null));
                       JsonNode invitations=objectMapper.readTree(callApi.callApiGet("/lol-lobby/v2/lobby", token, port, null)).get("invitations");
                       int members=invitations.size();
                       for (JsonNode jn:invitations){
                           if (!jn.get("state").asText().equals("Pending")){
                               members--;
                           }
                       }
                       if (members==0){
                           if (jsonNode.get("localMember").get("isLeader").asText().equals("true")){
                               if (jsonNode.get("gameConfig").get("showPositionSelector").asText().equals("true")){
                                   Map<String,Object> map=Map.of("firstPreference",autoSearchMatch.getFirstPosition(),"secondPreference",autoSearchMatch.getSecondPosition());
                                   callApi.callApiPut("/lol-lobby/v2/lobby/members/localMember/position-preferences",token,port,map);
                               }
                               matchMacking();
                               break;
                           }
                       }
                   }
               }
           }
           if (message.getPayload().contains("ReadyCheck")) {
               if (!Objects.isNull(autoAccecptMatch.getIsAutoAccecptMatch())&&(!autoAccecptMatch.getIsAutoAccecptMatch().isEmpty())&&autoAccecptMatch.getIsAutoAccecptMatch().equals("true")){
                   String Url= "/lol-matchmaking/v1/ready-check/accept";
                   callApi.callApiPost(Url,token,port,null);
                   System.out.println("自动接受匹配");
               }
           }
           if (message.getPayload().contains("ChampSelect")) {
               while (state.equals("ChampSelect")) {

                   if ((Objects.isNull(autoSwap)||Objects.isNull(autoSwap.getState())||autoSwap.getState().isEmpty()||autoSwap.getState().equals("false"))
                           &&(Objects.isNull(pickChampionId)||Objects.isNull(pickChampionId.getState())||pickChampionId.getState().isEmpty()||pickChampionId.getState().equals("stop"))
                           &&(Objects.isNull(banChampionId)||Objects.isNull(banChampionId.getState())||banChampionId.getState().isEmpty()||banChampionId.getState().equals("stop"))){
                       break;
                   }

                   if (!Objects.isNull(autoSwap)){
                       if (!Objects.isNull(autoSwap.getState())&&!autoSwap.getState().isEmpty()){
                           if (autoSwap.getState().equals("true")){
                               JsonNode jsonNodeAccecptSwap = objectMapper.readTree(callApi.callApiGet("/lol-champ-select/v1/session", token, port, null));
                               if (!jsonNodeAccecptSwap.get("pickOrderSwaps").isEmpty()){
                                   for (JsonNode jsonNode:jsonNodeAccecptSwap.get("pickOrderSwaps")){
                                       if (jsonNode.get("state").asText().equals("RECEIVED")){
                                           //还不好用
                                           System.out.println(callApi.callApiPost(
                                                   "/lol-champ-select/v1/session/trades/"+jsonNode.get("id").asInt()+"/accept",
                                                   token,
                                                   port,
                                                   null
                                           ));
                                           System.out.println(callApi.callApiPost(
                                                   "/lol-champ-select/v1/session/trades/"+jsonNode.get("cellId").asInt()+"/accept",
                                                   token,
                                                   port,
                                                   null
                                           ));
                                       }
                                   }
                               }
                           }
                       }
                   }

                   if ((!Objects.isNull(pickChampionId.getState()) && pickChampionId.getState().equals("start")) ||( !Objects.isNull(banChampionId.getState())&&banChampionId.getState().equals("start"))) {
                       JsonNode jsonNode = objectMapper.readTree(callApi.callApiGet("/lol-champ-select/v1/session", token, port, null));
                       if (!Objects.isNull(jsonNode.get("message")) &&(jsonNode.get("message").asText().equals("No active delegate")||jsonNode.get("errorCode").asText().equals("RPC_ERROR"))){
                           break;
                       }
                       //phase:PLANING
                       if (!Objects.isNull(jsonNode.get("timer"))) {
                           if ((!Objects.isNull(jsonNode.get("timer"))&&
                                   !Objects.isNull(jsonNode.get("timer").get("phase"))&&
                                   jsonNode.get("timer").get("phase").asText().equals("FINALIZATION"))||
                                   (!Objects.isNull(jsonNode.get("timer"))&&!Objects.isNull(jsonNode.get("timer").get("phase"))&&jsonNode.get("timer").get("phase").asText().equals("GAME_STARTING"))) {
                                 break;
                           }
                           if (jsonNode.get("timer").get("phase").asText().equals("BAN_PICK")) {
                               JsonNode jsonNode1 = jsonNode.get("actions");
                               int cellId = jsonNode.get("localPlayerCellId").asInt();
                               for (JsonNode subArray : jsonNode1) {
                                   int id;
                                   Map<String, Object> map;
                                   for (JsonNode jsonNode2 : subArray) {
                                       if (jsonNode2.get("actorCellId").asInt() == cellId && jsonNode2.get("completed").asText().equals("false")) {
                                           if (jsonNode2.get("type").asText().equals("pick") && !Objects.isNull(pickChampionId.getState()) && pickChampionId.getState().equals("start")) {
                                               map = Map.of(
                                                       "actorCellId", cellId,
                                                       "championId", pickChampionId.getChampionId(),
                                                       "completed", "true",
                                                       "id", jsonNode2.get("id").asText(),
                                                       "isAllyAction", "true",
                                                       "type", "pick"
                                               );
                                               id = jsonNode2.get("id").asInt();
                                               callApi.callApiPatch("/lol-champ-select/v1/session/actions/" + id, token, port, map);
                                           }
                                           if (jsonNode2.get("type").asText().equals("ban") && !Objects.isNull(banChampionId.getState()) && banChampionId.getState().equals("start")) {
                                               map = Map.of(
                                                       "actorCellId", cellId,
                                                       "championId", banChampionId.getChampionId(),
                                                       "completed", "true",
                                                       "id", jsonNode2.get("id").asText(),
                                                       "isAllyAction", "true",
                                                       "type", "ban"
                                               );
                                               id = jsonNode2.get("id").asInt();
                                               callApi.callApiPatch("/lol-champ-select/v1/session/actions/" + id, token, port, map);
                                           }
                                       }
                                   }
                               }
                           }
                       }
                   }
               }
           }
           if (message.getPayload().contains("InProgress")) {
           }
           if (message.getPayload().contains("PreEndOfGame")) {
               if (!Objects.isNull(autoContinueNextGame)&&!Objects.isNull(autoContinueNextGame.getAutoContinueNextGame())&&!autoContinueNextGame.getAutoContinueNextGame().isEmpty()) {
                   if (autoContinueNextGame.getAutoContinueNextGame().equals("true")) {
                       String Url1 = "/lol-honor-v2/v1/honor-player";
                       Map<String,Object> param=Map.of("honorCategory","");
                       callApi.callApiPost(Url1, token, port, param);
                       System.out.println("游戏结束,荣誉加一");
                   }
               }
           }
           if (message.getPayload().contains("EndOfGame")){
               if (!Objects.isNull(autoContinueNextGame)&&!Objects.isNull(autoContinueNextGame.getAutoContinueNextGame())&&!autoContinueNextGame.getAutoContinueNextGame().isEmpty()) {
                   if (autoContinueNextGame.getAutoContinueNextGame().equals("true")) {
                       String Url= "/lol-lobby/v2/play-again";
                       callApi.callApiPost(Url,token,port,null);
                   }
               }
           }
       }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
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
    public boolean matchMacking( ) throws JsonProcessingException {
        // 发送消息
        System.out.println("dasdasdasdasdasdas");
        CallApi callApi=new CallApi();
        ObjectMapper objectMapper = new ObjectMapper();
        String  port=getGlobalTokenAndPort.getPort();
        String  token=getGlobalTokenAndPort.getToken();
        JsonNode jsonNode=objectMapper.readTree(callApi.callApiPost("/lol-lobby/v2/lobby/matchmaking/search",token,port,null));
        if (!Objects.isNull(jsonNode.get("message"))){
            if (!jsonNode.get("message").asText().isEmpty()){
                return jsonNode.get("message").asText().equals("GATEKEEPER_RESTRICTED");
            }
        }
        return true;
    }
}
