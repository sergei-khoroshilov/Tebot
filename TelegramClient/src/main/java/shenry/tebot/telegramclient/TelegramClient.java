package shenry.tebot.telegramclient;

import shenry.tebot.telegramclient.types.Message;
import shenry.tebot.telegramclient.types.Update;
import shenry.tebot.telegramclient.types.User;
import shenry.tebot.telegramclient.requests.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by shenry on 14.11.2015.
 */
public interface TelegramClient {
    User getMe() throws IOException;

    List<Update> getUpdates() throws IOException;

    List<Update> getUpdates(GetUpdatesRequest request) throws IOException;

    Message sendMessage(SendMessageRequest request) throws IOException;
}
