package shenry.tebot.argconverter;

import shenry.tebot.telegramclient.types.Message;
import shenry.tebot.telegramclient.types.Update;

import java.util.HashMap;
import java.util.Map;

public class ArgConverterFactory {

    private final Map<Class, ArgConverter> converters = new HashMap<>();

    public ArgConverterFactory() {
        converters.put(Update.class, new UpdateArgConverter());
        converters.put(Message.class, new MessageArgConverter());
        converters.put(String.class, new StringArgConverter());
    }

    public ArgConverter get(Class clazz) {
        return converters.get(clazz);
    }
}
