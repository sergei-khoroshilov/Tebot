package shenry.tebot;

import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shenry.tebot.telegramclient.types.Update;

import java.util.Collection;

@Component
public class UpdateHandler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateHandler.class);

    private final CommandExtractor commandExtractor;
    private final TebotControllersProvider controllersProvider;

    private final Multimap<String, UpdateMethodInvoker> handlers;

    @Autowired
    public UpdateHandler(CommandExtractor commandExtractor, TebotControllersProvider controllersProvider) {
        this.commandExtractor = commandExtractor;
        this.controllersProvider = controllersProvider;

        handlers = controllersProvider.loadHandlers();
    }

    public void handle(Update update) {
        logger.debug("Handling update {}", update);

        String command = commandExtractor.getCommand(update.getMessage().getText());

        Predicate<String> commandPredicate = key -> key.equalsIgnoreCase(command)
                                                 || key.isEmpty();

        Collection<UpdateMethodInvoker> methods = Multimaps.filterKeys(handlers, commandPredicate)
                                                           .values();

        for (UpdateMethodInvoker method : methods) {
            try {
                method.invoke(update);
            } catch (Exception ex) {
                logger.error("Error handling update", ex);
            }
        }
    }
}
