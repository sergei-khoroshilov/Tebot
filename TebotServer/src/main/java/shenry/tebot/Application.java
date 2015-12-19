package shenry.tebot;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import shenry.tebot.config.ApplicationConfiguration;


public class Application
{
    // TODO determine threads count from proc count
    // TODO write logs to file

    public static void main ( String[] args ) throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);

        TebotServer server = ctx.getBean(TebotServer.class);
        server.start();

        Thread.currentThread().sleep(50000);
        server.stop();
    }
}
