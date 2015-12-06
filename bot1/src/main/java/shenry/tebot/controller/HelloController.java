package shenry.tebot.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import shenry.tebot.annotation.TebotController;
import shenry.tebot.annotation.TebotMapping;
import shenry.tebot.annotation.TebotMappings;
import shenry.tebot.api.Message;
import shenry.tebot.api.User;

@Component
@TebotController
public class HelloController {

    @TebotMapping("/hello")
    @TebotMapping("")
    public String hello() {
        return "Hello world";
    }

    @TebotMapping("/helloMessage")
    public Message helloMessage() {
        Message message = new Message();
        message.setSender(new User(1, "shenry", "", ""));
        return message;
    }
}
