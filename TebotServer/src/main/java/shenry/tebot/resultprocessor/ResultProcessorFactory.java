package shenry.tebot.resultprocessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shenry.tebot.telegramclient.TelegramClient;
import shenry.tebot.telegramclient.requests.SendMessageRequest;

import java.util.HashMap;
import java.util.Map;

@Component
public class ResultProcessorFactory {

    private final Map<Class, ResultProcessor> processors = new HashMap<>();

    @Autowired
    public ResultProcessorFactory(TelegramClient client) {
        processors.put(Void.class, new NoOpResultProcessor());
        processors.put(String.class, new StringResultProcessor(client));
        processors.put(SendMessageRequest.class, new MessageResultProcessor(client));
    }

    public ResultProcessor get(Class clazz) {
        return processors.get(clazz);
    }
}
