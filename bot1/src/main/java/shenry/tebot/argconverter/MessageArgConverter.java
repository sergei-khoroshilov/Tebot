package shenry.tebot.argconverter;

import shenry.tebot.telegramclient.types.Update;

public class MessageArgConverter implements ArgConverter {

    @Override
    public Object convert(String command, Update update) {
        return update.getMessage();
    }
}
