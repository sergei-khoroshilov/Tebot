package shenry.tebot.config;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import shenry.tebot.Application;
import shenry.tebot.telegramclient.HttpTelegramClient;
import shenry.tebot.telegramclient.TelegramClient;

import java.net.Proxy;

@Configuration
@ComponentScan(value = "shenry.tebot")
public class ApplicationConfiguration {

    @Autowired
    private ApplicationSettings settings;

    @Bean
    public static PropertySourcesPlaceholderConfigurer getPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public TelegramClient getTelegramClient() {
        HttpTelegramClient.Builder builder = HttpTelegramClient.getBuilder();
        builder.setToken(settings.getTelegramToken())
               .setProxy(Proxy.NO_PROXY);

        if (!Strings.isNullOrEmpty(settings.getTelegramApiAddress())) {
            builder.setApiAddress(settings.getTelegramApiAddress());
        }

        return builder.build();
    }
}
