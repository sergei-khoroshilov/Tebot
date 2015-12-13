package shenry.tebot.argconverter;

import shenry.tebot.telegramclient.types.Update;

public class UpdateArgConverter implements ArgConverter {

    @Override
    public Object convert(String command, Update update) {
        return update;
    }
}
