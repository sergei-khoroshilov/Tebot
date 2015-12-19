package shenry.tebot;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shenry.tebot.config.ApplicationSettings;
import shenry.tebot.telegramclient.TelegramClient;
import shenry.tebot.telegramclient.requests.GetUpdatesRequest;
import shenry.tebot.telegramclient.types.Update;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TebotServer {
    private static final Logger logger = LoggerFactory.getLogger(TebotServer.class);

    private final UpdateHandler updateHandler;
    private final TelegramClient client;

    private Thread listenThread;
    private final ExecutorService executorService;
    private final AtomicBoolean started = new AtomicBoolean(false);

    @Autowired
    public TebotServer(UpdateHandler updateHandler, TelegramClient client, ApplicationSettings settings) {
        this.updateHandler = updateHandler;
        this.client = client;

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
                        executorService.submit(() -> updateHandler.handle(update));
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
        final long stopTime = System.nanoTime() * 1000;   // Current stop time in milliseconds

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
}
