package shenry.tebot.telegramclient.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum ChatType {
    PRIVATE("private"),
    GROUP("group"),
    SUPERGROUP("supergroup"),
    CHANNEL("channel");

    private final String value;

    private static final Map<String, ChatType> allTypes = new HashMap<String, ChatType>();

    static {
        for (ChatType type : ChatType.values()) {
            allTypes.put(type.value, type);
        }
    }

    private ChatType(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    @JsonCreator
    public static ChatType forValue(String value) {
        return allTypes.get(value.toLowerCase());
    }

    @Override
    public String toString() {
        return value;
    }
}
