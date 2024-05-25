package com.realtimeCodeEditorBackend.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = Action.ActionDeserializer.class)
public enum Action {
    JOIN("JOIN"),
    JOINED("JOINED"),
    DISCONNECTED("DISCONNECTED"),
    CODE_CHANGE("CODE_CHANGE"),
    SYNC_CODE("SYNC_CODE"),
    LEAVE("LEAVE");

    private final String action;

    Action(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public static Action fromString(String action) {
        for (Action a : Action.values()) {
            if (a.getAction().equalsIgnoreCase(action)) {
                return a;
            }
        }
        throw new IllegalArgumentException("No enum constant " + Action.class.getCanonicalName() + " with action " + action);
    }

    static class ActionDeserializer extends JsonDeserializer<Action> {
        @Override
        public Action deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String action = jsonParser.getText();
            return Action.fromString(action);
        }
    }
}
