package shenry.tebot.telegramclient;

/**
 * Created by shenry on 15.11.2015.
 */
public enum TelegramMethod {
    GET_ME("/getMe"),
    SEND_MESSAGE("/sendMessage"),
    GET_UPDATES("/getUpdates");

    private String value;

    private TelegramMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
