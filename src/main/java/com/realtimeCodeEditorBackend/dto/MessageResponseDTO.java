package com.realtimeCodeEditorBackend.dto;

import com.realtimeCodeEditorBackend.enums.Action;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDTO {
    private String username;
    private String sessionId;
    //    private  String data;
    private List<UserSession> sessionUser;
    private  String roomId;
    private Action type;
}
