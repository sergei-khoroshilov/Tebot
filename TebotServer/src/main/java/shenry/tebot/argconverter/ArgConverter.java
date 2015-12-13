package shenry.tebot.argconverter;

import shenry.tebot.telegramclient.types.Update;

/**
 * Used to convert an {@link Update} instance to parameters for
 * {@link shenry.tebot.annotation.TebotMapping @TebotMapping} methods.
 */
public interface ArgConverter {
    Object convert(String command, Update update);
}
