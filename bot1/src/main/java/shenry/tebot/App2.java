package shenry.tebot;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by shenry on 14.11.2015.
 */
public class App2 {
    public static void main(String[] params) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        final int n = 50;

        CountDownLatch cdl = new CountDownLatch(n/2);
        AtomicInteger cur = new AtomicInteger(0);

        for (int i = 0; i < n; i++) {

            Runnable worker = () -> {
                try {
                    Thread.sleep(500);
                    System.out.println("done " + cur.incrementAndGet() + " thr = " + Thread.currentThread().getId());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("interrupted");
                } finally {
                    cdl.countDown();
                }
            };

            Future future = new FutureTask(worker, null);

            executorService.submit(() -> {
                try {
                    Object res = future.get(600 - 200 * cur.get() % 2 , TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            });
        }

        cdl.await();
        executorService.shutdownNow();
        System.out.println("done all");
    }
}
