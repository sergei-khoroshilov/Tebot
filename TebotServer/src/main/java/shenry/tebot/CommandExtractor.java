package shenry.tebot;

import org.springframework.stereotype.Component;

@Component
public class CommandExtractor {
    /**
     * Get command from message
     */
    public String getCommand(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        message = message.trim();

        for (int i = 0; i < message.length(); i++) {
            Character ch = message.charAt(i);

            if (Character.isSpaceChar(ch) || Character.isWhitespace(ch)) {
                break;
            }

            sb.append(ch);
        }

        return sb.toString();
    }

    /**
     * Return message without command
     */
    public String removeCommand(String message, String command) {
        if (message == null) {
            throw new IllegalArgumentException("message cannot be null");
        }

        if(command == null || command.isEmpty()) {
            return message;
        }

        message = message.trim();
        command = command.trim();

        if (!message.startsWith(command)) {
            throw new IllegalArgumentException("message should contain command");
        }

        return message.substring(command.length()).trim();
    }
}
