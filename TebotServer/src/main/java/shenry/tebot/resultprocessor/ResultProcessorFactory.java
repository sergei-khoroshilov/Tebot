package shenry.tebot.resultprocessor;

import shenry.tebot.telegramclient.TelegramClient;
import shenry.tebot.telegramclient.requests.SendMessageRequest;
import shenry.tebot.telegramclient.types.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenry on 09.12.2015.
 */
public class ResultProcessorFactory {

    private final Map<Class, ResultProcessor> processors = new HashMap<>();

    public ResultProcessorFactory(TelegramClient client) {
        processors.put(Void.class, new NoOpResultProcessor());
        processors.put(String.class, new StringResultProcessor(client));
        processors.put(SendMessageRequest.class, new MessageResultProcessor(client));
    }

    public ResultProcessor get(Class clazz) {
        return processors.get(clazz);
    }
}
