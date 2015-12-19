package shenry.tebot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:app.properties")
public class ApplicationSettings {

    @Value("${telegram.api.token}")
    @Getter
    private String telegramToken;

    @Value("${telegram.api.server:}")
    @Getter
    private String telegramApiAddress;

    @Value("${worker.threads.count:2}")
    @Getter
    private int workerThreadsCount;
}
