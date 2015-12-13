package shenry.tebot.argconverter;

import shenry.tebot.CommandExtractor;
import shenry.tebot.telegramclient.types.Update;

/**
 * Get message text without command.
 */
public class StringArgConverter implements ArgConverter {

    private CommandExtractor commandExtractor = new CommandExtractor();

    @Override
    public Object convert(String command, Update update) {
        return commandExtractor.removeCommand(update.getMessage().getText(), command);
    }
}
