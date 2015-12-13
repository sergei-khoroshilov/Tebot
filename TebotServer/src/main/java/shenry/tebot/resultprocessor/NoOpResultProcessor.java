package shenry.tebot.resultprocessor;

import shenry.tebot.telegramclient.types.Update;

/**
 * Created by shenry on 09.12.2015.
 */
public class NoOpResultProcessor implements ResultProcessor {
    @Override
    public void process(Object obj, Update update) {
    }
}
