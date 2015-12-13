package shenry.tebot.resultprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shenry.tebot.telegramclient.TelegramClient;
import shenry.tebot.telegramclient.requests.SendMessageRequest;
import shenry.tebot.telegramclient.types.Message;
import shenry.tebot.telegramclient.types.Update;

import java.io.IOException;

/**
 * Created by shenry on 09.12.2015.
 */
public class MessageResultProcessor implements ResultProcessor {
    private final static Logger logger = LoggerFactory.getLogger(MessageResultProcessor.class);

    private TelegramClient client;

    public MessageResultProcessor(TelegramClient client) {
        this.client = client;
    }

    @Override
    public void process(Object obj, Update update) {
        try {
            Message result = client.sendMessage((SendMessageRequest)obj);
            logger.debug("Send message {}", result);
        } catch (IOException ex) {
            logger.error("Error sending message " + obj, ex);
        }
    }
}
