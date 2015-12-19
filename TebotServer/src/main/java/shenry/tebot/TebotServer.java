package shenry.tebot;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import shenry.tebot.annotation.TebotController;
import shenry.tebot.annotation.TebotMapping;
import shenry.tebot.argconverter.ArgConverter;
import shenry.tebot.argconverter.ArgConverterFactory;
import shenry.tebot.config.ApplicationSettings;
import shenry.tebot.resultprocessor.ResultProcessor;
import shenry.tebot.resultprocessor.ResultProcessorFactory;
import shenry.tebot.telegramclient.TelegramClient;
import shenry.tebot.telegramclient.requests.GetUpdatesRequest;
import shenry.tebot.telegramclient.types.Update;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TebotServer {
    private static final Logger logger = LoggerFactory.getLogger(TebotServer.class);

    private final CommandExtractor commandExtractor;
    private final ArgConverterFactory argConverterFactory;
    private final ResultProcessorFactory resultProcessorFactory;
    private final TelegramClient client;
    private final ApplicationContext applicationContext;

    private final Multimap<String, UpdateMethodInvoker> handlers;

    private Thread listenThread;
    private final ExecutorService executorService;
    private final AtomicBoolean started = new AtomicBoolean(false);

    @Autowired
    public TebotServer(TelegramClient client,
                       ApplicationSettings settings,
                       CommandExtractor commandExtractor,
                       ArgConverterFactory argConverterFactory,
                       ResultProcessorFactory resultProcessorFactory,
                       ApplicationContext applicationContext) {
        this.client = client;
        this.commandExtractor = commandExtractor;
        this.argConverterFactory = argConverterFactory;
        this.resultProcessorFactory = resultProcessorFactory;
        this.applicationContext = applicationContext;

        handlers = loadHandlers(applicationContext);

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("TebotServerPool-thread-%d")
                .build();
        executorService = Executors.newFixedThreadPool(settings.getWorkerThreadsCount(), threadFactory);
    }

    public void start() {
        logger.info("Starting TebotServer");

        Runnable listenRunnable = () -> {
            started.set(true);
            int lastOffset = -1;

            while (started.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    GetUpdatesRequest updateParams = new GetUpdatesRequest();
                    updateParams.setOffset(lastOffset + 1);

                    List<Update> updates = client.getUpdates(updateParams);

                    for (Update update : updates) {
                        executorService.submit(() -> handleRequest(update));
                        lastOffset = Math.max(lastOffset, update.getId());
                    }
                } catch (Exception ex) {
                    logger.error("Error getting updates", ex);

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException exInterrupted) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };

        listenThread = new Thread(listenRunnable, "TebotServerListener");
        listenThread.start();

        logger.info("TebotServer started");
    }

    public void stop() {
        logger.info("Stopping TebotServer");

        final long WAIT_STOP_TIME_MS = 2000;        // Max time for safe waiting
        long stopTime = System.nanoTime() * 1000;   // Current stop time in milliseconds

        started.set(false);

        // Stop executorService threads
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(WAIT_STOP_TIME_MS, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        // Stop listen thread if it is not finished
        try {
            long stopDuration = System.nanoTime() * 1000 - stopTime;
            if (stopDuration < WAIT_STOP_TIME_MS) {
                listenThread.join(WAIT_STOP_TIME_MS - stopDuration);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        if(listenThread.isAlive()) {
            listenThread.interrupt();
        }

        logger.info("TebotServer stopped");
    }

    private void handleRequest(Update update) {
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

    private Multimap<String, UpdateMethodInvoker> loadHandlers(ApplicationContext ctx) {
        Multimap<String, UpdateMethodInvoker> handlers = ArrayListMultimap.create();

        Map<String, Object> beans = ctx.getBeansWithAnnotation(TebotController.class);

        for (Object instance : beans.values()) {
            Method[] methods = instance.getClass()
                                       .getDeclaredMethods();

            for (Method method : methods) {
                ArgConverter[] argConverters = getArgConverters(method.getParameterTypes());
                ResultProcessor resultProcessor = getResultProcessor(method.getReturnType());

                TebotMapping[] annotations = method.getAnnotationsByType(TebotMapping.class);
                for (TebotMapping annotation : annotations) {
                    try {
                        String command = annotation.value().trim();

                        UpdateMethodInvoker methodInvoker = new UpdateMethodInvoker
                                (
                                        command,
                                        method,
                                        instance,
                                        argConverters,
                                        resultProcessor
                                );

                        handlers.put(command, methodInvoker);

                        logger.info("Added tebot handler \"{}\" - {}", command, method);
                    } catch (Exception ex) {
                        logger.error("Cannot create arg converters for method " + method, ex);
                        continue;
                    }
                }
            }
        }

        return handlers;
    }

    private ArgConverter[] getArgConverters(Class[] parameterTypes) {
        if (parameterTypes == null) {
            return new ArgConverter[0];
        }

        ArgConverter[] converters = new ArgConverter[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            ArgConverter converter = argConverterFactory.get(parameterTypes[i]);

            if (converter == null) {
                throw new IllegalArgumentException("Cannot find arg converter for type " + parameterTypes[i]);
            }

            converters[i] = converter;
        }

        return converters;
    }

    private ResultProcessor getResultProcessor(Class returnType) {
        ResultProcessor processor = resultProcessorFactory.get(returnType);

        if (processor == null) {
            throw new IllegalArgumentException("Cannot find result processor for type " + returnType);
        }

        return processor;
    }
}
