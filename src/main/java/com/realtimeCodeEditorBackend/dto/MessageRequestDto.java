package com.realtimeCodeEditorBackend.dto;

import com.realtimeCodeEditorBackend.enums.Action;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequestDto {
    private String username;
//    private  String data;
    private String sessionId;
    private  String roomId;
    private Action type;

}
