package shenry.tebot.controller;

import org.springframework.stereotype.Component;
import shenry.tebot.annotation.TebotController;
import shenry.tebot.annotation.TebotMapping;
import shenry.tebot.telegramclient.requests.SendMessageRequest;
import shenry.tebot.telegramclient.types.*;

@Component
@TebotController
public class HelloController {

    @TebotMapping("/hello")
    @TebotMapping("")
    public String hello() {
        return "Hello world";
    }

    @TebotMapping("/helloMessage")
    public SendMessageRequest helloMessage(Update update) {
        SendMessageRequest message = new SendMessageRequest();
        message.setChatId(Integer.toString(update.getMessage().getChat().getId()));
        message.setText("Hello, world");

        return message;
    }

    @TebotMapping("/echo")
    public String echo(String message) {
        return message;
    }
}
