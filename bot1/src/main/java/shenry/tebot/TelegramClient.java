package shenry.tebot;

import shenry.tebot.api.Message;
import shenry.tebot.api.Update;
import shenry.tebot.api.User;
import shenry.tebot.http.*;

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
