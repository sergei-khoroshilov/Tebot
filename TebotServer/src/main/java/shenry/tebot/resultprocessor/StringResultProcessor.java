package shenry.tebot.resultprocessor;

import shenry.tebot.telegramclient.TelegramClient;
import shenry.tebot.telegramclient.requests.SendMessageRequest;
import shenry.tebot.telegramclient.types.Update;

/**
 * Created by shenry on 09.12.2015.
 */
public class StringResultProcessor extends MessageResultProcessor implements ResultProcessor {

    public StringResultProcessor(TelegramClient client) {
        super(client);
    }

    @Override
    public void process(Object obj, Update update) {
        SendMessageRequest request = new SendMessageRequest();

        request.setChatId(Integer.toString(update.getMessage().getChat().getId()));
        request.setReplyToMessageId(update.getMessage().getId());
        request.setText((String)obj);

        super.process(request, update);
    }
}
