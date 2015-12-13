package shenry.tebot;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = "shenry.tebot")
public class Application
{
    // TODO move api key to config file
    // TODO allow multiple api keys
    // TODO determine threads count from proc count
    // TODO write logs to file
    // TODO move configuration to separate class
    // TODO use Spring IOC-container instead of explicit instance creation

    public static void main ( String[] args ) throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(Application.class);
        TebotServer server = new TebotServer("Api key here", ctx, 2);

        server.start();

        Thread.currentThread().sleep(50000);
        server.stop();
    }
}
