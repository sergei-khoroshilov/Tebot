package shenry.tebot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import shenry.tebot.telegramclient.HttpTelegramClient;
import shenry.tebot.telegramclient.TelegramClient;
import shenry.tebot.telegramclient.types.User;

/**
 * Hello world!
 *
 */

@Configuration
@ComponentScan(value = "shenry.tebot")
public class App 
{
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main ( String[] args ) throws Exception
    {
        logger.debug("Hello, world!");

        TelegramClient client = new HttpTelegramClient("Api key here");

        User result = client.getMe();
        System.out.println(result);


/*
        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setChatId("85226090");
        sendMessageRequest.setText("Hello from bot");
        Message sendMessageResponse = client.sendMessage(sendMessageRequest);

        System.out.println(sendMessageRequest);
        System.out.println(sendMessageResponse);
*/
/*
        List<Update> updates = client.getUpdates();

        for (Update update : updates) {
            System.out.println(update);
        }
*/
    }
}
