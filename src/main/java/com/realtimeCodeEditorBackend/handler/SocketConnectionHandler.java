package com.realtimeCodeEditorBackend.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtimeCodeEditorBackend.dto.MessageRequestDto;
import com.realtimeCodeEditorBackend.dto.MessageResponseDTO;
import com.realtimeCodeEditorBackend.dto.UserSession;
import com.realtimeCodeEditorBackend.enums.Action;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

public class SocketConnectionHandler extends TextWebSocketHandler {

    private Map<String, Set<WebSocketSession>> roomIdSessionMap = new HashMap<>();
    private Map<String, List<String>> sessionUserMap = new HashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void  afterConnectionEstablished (WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        System.out.println(session.getId() + " Connected");

    }

    @Override
    public  void  afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
//        if(sessionUserMap.containsKey(session.getId())) {
//            sessionUserMap.remove(session.getId());
//        }
        System.out.println(session.getId() + " Disconnected");
        System.out.println("session and status after disconnection: "+session + status);
        leaveRoom(session);
    }


    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
        
        System.out.println(message.getPayload());
        String payload = (String) message.getPayload();
        MessageRequestDto messageDto = objectMapper.readValue(payload, MessageRequestDto.class);

        if(messageDto.getType() == Action.JOIN) {
            if(messageDto.getRoomId() != null) {
                String roomId = messageDto.getRoomId();
                if(roomIdSessionMap.containsKey(roomId)) {
                    if(!roomIdSessionMap.get(roomId).contains(session)) {
                        roomIdSessionMap.get(roomId).add(session);
                    }
                } else {
                    Set<WebSocketSession> sessions = new HashSet<>();
                    sessions.add(session);
                    roomIdSessionMap.put(roomId, sessions);
                }
                updateSessionUserMap(session, messageDto);
            }
            System.out.println("-------------------------------------");
            System.out.println(roomIdSessionMap);
            System.out.println("sessionUserMap: " + sessionUserMap);
            System.out.println("######################################");
            broadcastMessageToRoom(session, messageDto, message);
        } else if (messageDto.getType() == Action.DISCONNECTED) {

        }


    }

    private void  updateSessionUserMap(WebSocketSession sender, MessageRequestDto messageRequestDto) {
        sessionUserMap.put(sender.getId(), new ArrayList<>(Arrays.asList(messageRequestDto.getUsername(), messageRequestDto.getRoomId())));
    }

    private  void  leaveRoom(WebSocketSession sender) throws IOException {
        if(sessionUserMap.containsKey(sender.getId())) {
            String username = sessionUserMap.get(sender.getId()).get(0);
            String roomId = sessionUserMap.get(sender.getId()).get(1);
            sessionUserMap.remove(sender.getId());

            if(roomIdSessionMap.containsKey(roomId) && roomIdSessionMap.get(roomId).contains(sender)) {
                Set<WebSocketSession> sessions = roomIdSessionMap.get(roomId);
                sessions.remove(sender);
                if(sessions.isEmpty()) {
                    roomIdSessionMap.remove(roomId);
                } else {
                    MessageResponseDTO broadcastDisconnectingMessage = new MessageResponseDTO();
                    broadcastDisconnectingMessage.setRoomId(roomId);
                    broadcastDisconnectingMessage.setType(Action.DISCONNECTED);
                    broadcastDisconnectingMessage.setUsername(username);
                    broadcastDisconnectingMessage.setSessionId(sender.getId());
                    String broadcastDisconnectingMessageJson = objectMapper.writeValueAsString(broadcastDisconnectingMessage);

                    for(WebSocketSession session : sessions) {
                        if(!session.equals(sender) && session.isOpen()) {
                            session.sendMessage(new TextMessage(broadcastDisconnectingMessageJson));
                        }
                    }
                }

            }

        }
    }

    private  void  joinRoom(WebSocketSession sender, MessageRequestDto messageDto) throws IOException {
        System.out.println("JOIN");

        MessageResponseDTO broadcastJoiningMessage = new MessageResponseDTO();
        broadcastJoiningMessage.setRoomId(messageDto.getRoomId());
        broadcastJoiningMessage.setType(Action.JOINED);
        broadcastJoiningMessage.setUsername(messageDto.getUsername());
        broadcastJoiningMessage.setSessionId(sender.getId());
        List<UserSession> userSessions = new ArrayList<>();
        for(WebSocketSession s : roomIdSessionMap.get(messageDto.getRoomId())) {
            if(sessionUserMap.containsKey(s.getId())) {
                userSessions.add(new UserSession(s.getId(), sessionUserMap.get(s.getId()).get(0)));
            }
        }
        broadcastJoiningMessage.setSessionUser(userSessions);

        String broadcastJoiningMessageJson = objectMapper.writeValueAsString(broadcastJoiningMessage);
        Set<WebSocketSession> sessions = roomIdSessionMap.get(messageDto.getRoomId());
        for(WebSocketSession session : sessions) {
            if(session.isOpen() && !session.equals(sender)) {
                session.sendMessage(new TextMessage(broadcastJoiningMessageJson));
            }
        }
    }

    private void broadcastMessageToRoom(WebSocketSession sender, MessageRequestDto messageDto, WebSocketMessage<?> message) throws Exception {
        Action type = messageDto.getType();
        switch (type) {
            case JOIN:
                joinRoom(sender, messageDto);
                break;
            case LEAVE:
                break;
            case SYNC_CODE:
                break;
            case CODE_CHANGE:
                break;
            case DISCONNECTED:
                break;
            case JOINED:
                break;
        }
    }
}