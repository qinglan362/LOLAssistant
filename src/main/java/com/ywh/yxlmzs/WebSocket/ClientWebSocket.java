package com.ywh.yxlmzs.WebSocket;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.CurrentHistory;
import com.ywh.yxlmzs.entity.MatchRecord;
import com.ywh.yxlmzs.entity.championSelect.MatchSimple;
import com.ywh.yxlmzs.entity.championSelect.OnePersonHistory;
import com.ywh.yxlmzs.service.GetGameFromGameId;
import com.ywh.yxlmzs.utils.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

import static java.lang.Thread.sleep;

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
    private SendTeamMatch sendTeamMatch;

    @Autowired
    public ClientWebSocket(GetGlobalTokenAndPort getGlobalTokenAndPort,
                           PickChampionId pickChampionId,
                           BanChampionId banChampionId,
                           AutoContinueNextGame autoContinueNextGame,
                           AutoAccecptMatch autoAccecptMatch,
                           AutoSearchMatch autoSearchMatch,
                           AutoSwap autoSwap,
                            SendTeamMatch sendTeamMatch
                           ) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.pickChampionId=pickChampionId;
        this.banChampionId=banChampionId;
        this.autoContinueNextGame=autoContinueNextGame;
        this.autoAccecptMatch=autoAccecptMatch;
        this.autoSearchMatch=autoSearchMatch;
        this.autoSwap=autoSwap;
        this.sendTeamMatch=sendTeamMatch;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
        session.sendMessage(new TextMessage("[5, \"OnJsonApiEvent_lol-champ-select_v1_session\"]"));
        session.sendMessage(new TextMessage("[5, \"OnJsonApiEvent_lol-gameflow_v1_gameflow-phase\"]"));
    }

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
           int sendMatch=0;
           System.out.println("state = " + state);

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
                       System.out.println("lobby = " + jsonNode);
                       JsonNode invitations=jsonNode.get("invitations");
                       System.out.println("invitations = " + invitations);
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
                               JsonNode membersNode=jsonNode.get("members");
                               int count=membersNode.size();
                                 for (JsonNode jn:membersNode){
                                      if (jn.get("ready").asText().equals("true")){
                                        count--;
                                      }
                                 }
                                 if (count==0){
                                     System.out.println("开始匹配");
                                     matchMacking();
                                     break;
                                 }
                           }else{
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

               }
           }
           if (message.getPayload().contains("ChampSelect")) {

               while (state.equals("ChampSelect")) {

                   boolean flag=true;

                   if (!Objects.isNull(sendTeamMatch)&&!Objects.isNull(sendTeamMatch.getState())&&sendTeamMatch.getState().equals("true")){
                       if (sendMatch==0) {
                           while (flag) {
                               JsonNode chatNodes = objectMapper.readTree(
                                       callApi.callApiGet(
                                               "/lol-chat/v1/conversations",
                                               token,
                                               port,
                                               null
                                       )
                               );
                               for (JsonNode chatNode : chatNodes) {
                                   if (chatNode.get("type").asText().equals("championSelect")) {
                                       getCurrentGameSummonersId(token, port, callApi,chatNode.get("id").asText());
                                       flag = false;
                                       sendMatch++;
                                   }
                               }
                           }
                       }
                   }


                   if ((Objects.isNull(autoSwap)||Objects.isNull(autoSwap.getState())||autoSwap.getState().isEmpty()||autoSwap.getState().equals("false"))
                           &&(Objects.isNull(pickChampionId)||Objects.isNull(pickChampionId.getState())||pickChampionId.getState().isEmpty()||pickChampionId.getState().equals("stop"))
                           &&(Objects.isNull(banChampionId)||Objects.isNull(banChampionId.getState())||banChampionId.getState().isEmpty()||banChampionId.getState().equals("stop"))){
                       break;
                   }

                   if (!Objects.isNull(autoSwap)){
                       if (!Objects.isNull(autoSwap.getState())&&!autoSwap.getState().isEmpty()){
                           if (autoSwap.getState().equals("true")){
                               JsonNode jsonNodeAccecptSwap = objectMapper.readTree(callApi.callApiGet("/lol-champ-select/v1/session", token, port, null));
                               if (! jsonNodeAccecptSwap.get("pickOrderSwaps").isEmpty()){
                                   for (JsonNode jsonNode:jsonNodeAccecptSwap.get("pickOrderSwaps")){
                                       if (jsonNode.get("state").asText().equals("RECEIVED")){
                                           //还不好用
                //{"cellId":0,"id":15,"state":"RECEIVED"}
                                           /*
                                               # 同意交换
    @retry()
    async def acceptTrade(self, id):
        res = await self.__post(f"/lol-champ-select/v1/session/trades/{id}/accept")
        return await res.json()

    # 备战席交换
    async def benchSwap(self, championId):
        res = await self.__post(f"/lol-champ-select/v1/session/bench/swap/{championId}")
        return await res.json()
                                            */
                                           System.out.println(jsonNode);
                                           System.out.println(callApi.callApiPost(
                                                   "/lol-champ-select/v1/session/trades/"+jsonNode.get("id").asInt()+"/accept",
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
           if (message.getPayload().contains("InProgress")) {}
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
           if (message.getPayload().contains("WaitingForStats")) {
               System.out.println(message.getPayload());
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
        System.out.println("jsonNode = " + jsonNode);
        if (!Objects.isNull(jsonNode.get("message"))){
            if (!jsonNode.get("message").asText().isEmpty()){
                return jsonNode.get("message").asText().equals("GATEKEEPER_RESTRICTED");
            }
        }
        return true;
    }
    public void getCurrentGameSummonersId(String token,String port,CallApi callApi,String id) throws IOException, InterruptedException {

        List<String> puuids=new ArrayList<>();
        String url="/lol-champ-select/v1/session";
        String result=callApi.callApiGet(url,token,port,null);
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(result);
        System.out.println(rootNode);

        JsonNode myTeam=rootNode.get("myTeam");

        String myselfPuuid=objectMapper.readTree(callApi.callApiGet("/lol-summoner/v1/current-summoner",token,port,null)).get("puuid").asText();

        if (myTeam.isArray()) {
            for (JsonNode summonerNode : myTeam) {
                if (!Objects.equals(summonerNode.get("puuid").asText(), "")) {
                    if (!Objects.equals(summonerNode.get("puuid").asText(), myselfPuuid)){
                        puuids.add(summonerNode.get("puuid").asText());
                    }
                }
            }
        }

        List<OnePersonHistory> AllMyTeamMatchRecords = new ArrayList<>();

        int kk=0;
        for (String puuid : puuids) {

//            JsonNode s=objectMapper.readTree(callApi.callApiGet(
//                    "/lol-summoner/v2/summoners/puuid/"+puuid,
//                    token,
//                    port,
//                    null
//            ));
//
//            System.out.println(s);

//            if (isLuanDou){
//                callApi.callApiPost(
//                        "/lol-chat/v1/conversations/" +id + "/messages",
//                        token,
//                        port,
//                        Map.of("body",s.get("numberOfRolls").asText())
//                );
//            }

            OnePersonHistory onePersonHistory = new OnePersonHistory();
            JsonNode current=objectMapper.readTree(callApi.callApiGet("/lol-summoner/v2/summoners/puuid/"+puuid,token,port,null));
            onePersonHistory.setName(current.get("gameName").asText());

            List<MatchSimple> matchSimples = new ArrayList<>();
            String matchUrl = "/lol-match-history/v1/products/lol/" + puuid + "/matches";
            Map<String, Object> params = new HashMap<>();
            params.put("begIndex", "0");
            params.put("endIndex", "4");

            JsonNode games = objectMapper.readTree(callApi.callApiGet(matchUrl, token, port, params)).get("games").get("games");

            List<String> gameIds= new ArrayList<>();
            for (JsonNode game : games) {
                gameIds.add(game.get("gameId").asText());
            }
            for (String gameId : gameIds) {
                JsonNode gameDetail = objectMapper.readTree(getGameFromGameId(gameId,callApi));
                List<JsonNode> participants = Collections.singletonList(gameDetail.get("participants"));
                List<JsonNode> participantIdentities = Collections.singletonList(gameDetail.get("participantIdentities"));
                for (int i = 0; i < participants.size(); i++) {
                    for (int j = 0; j < participantIdentities.get(i).size(); j++) {
                        if (participantIdentities.get(i).get(j).get("player").get("puuid").asText().equals(puuid)) {
                            MatchSimple matchSimple = new MatchSimple();
                            matchSimple.setDeaths(participants.get(i).get(j).get("stats").get("deaths").asInt());
                            matchSimple.setKills(participants.get(i).get(j).get("stats").get("kills").asInt());
                            matchSimple.setAssists(participants.get(i).get(j).get("stats").get("assists").asInt());
                            matchSimples.add(matchSimple);
                        }
                    }
                }
            }
            onePersonHistory.setOneHistory(matchSimples);
            String name="";
            String match="";
            name+=onePersonHistory.getName();
            for (int i=0;i<onePersonHistory.getOneHistory().size();i++){
                match+="("+onePersonHistory.getOneHistory().get(i).getKills()+"/"+onePersonHistory.getOneHistory().get(i).getDeaths()+"/"+onePersonHistory.getOneHistory().get(i).getAssists()+")"+"||||";
            }
            kk++;
            System.out.println(callApi.callApiPost(
                    "/lol-chat/v1/conversations/" +id + "/messages",
                    token,
                    port,
                    Map.of("body",name+" "+match)
            ));
            if (kk==onePersonHistory.getOneHistory().size()){
                System.out.println(callApi.callApiPost(
                        "/lol-chat/v1/conversations/" +id + "/messages",
                        token,
                        port,
                        Map.of("body",name+" "+match)
                ));
            }
        }
    }
    public String getGameFromGameId(String gameId,CallApi callApi) throws IOException {
        String url = "/lol-match-history/v1/games/" + gameId;
        String token = getGlobalTokenAndPort.getToken();
        String port = getGlobalTokenAndPort.getPort();
        return callApi.callApiGet(url, token, port, null);
    }
}
