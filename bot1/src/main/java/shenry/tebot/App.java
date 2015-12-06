package shenry.tebot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import shenry.tebot.annotation.TebotController;
import shenry.tebot.annotation.TebotMapping;
import shenry.tebot.api.Message;
import shenry.tebot.api.Update;
import shenry.tebot.controller.HelloController;
import shenry.tebot.http.GetUpdatesRequest;
import shenry.tebot.http.SendMessageRequest;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;

/**
 * Hello world!
 *
 */

@Configuration
@ComponentScan(value = "shenry.tebot")
public class App 
{
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    private static Map<String, MethodWithClass> mappings = new HashMap<>();

    private static final BlockingQueue<Update> updatesQueue = new ArrayBlockingQueue<Update>(1000);


    private static class MethodWithClass {
        private Method method;
        private Object obj;

        private ArgsConverter argsConverter;
        private ResultProcessor resultProcessor;

        public MethodWithClass() {
        }

        public MethodWithClass(Method method, Object obj) {
            this.method = method;
            this.obj = obj;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Object getObj() {
            return obj;
        }

        public void setObj(Object obj) {
            this.obj = obj;
        }

        public void setArgsConverter(ArgsConverter argsConverter) {
            this.argsConverter = argsConverter;
        }

        public void setResultProcessor(ResultProcessor resultProcessor) {
            this.resultProcessor = resultProcessor;
        }

        public Object invoke(Object... args) throws Exception {
            return method.invoke(obj, args);
        }

        public void invoke(Update update) throws Exception {
            Class<?> paramClass = Arrays.stream(method.getParameterTypes())
                                        .findFirst()
                                        .orElse(Void.class);

            ArgsConverter converter = oneArgsConverters.get(paramClass);

            Object result = method.invoke(obj, converter.convert(update));
            resultProcessor.process(result, update);
        }
    }

    private static void handleRequest(Update update) {
        logger.debug("handling update {}", update);

        for (MethodWithClass method : mappings.values()) {
            try {
                method.invoke(update);
            } catch (Exception ex) {
                logger.error("error handling update", ex);
            }
        }
    }

    private static Map<String, MethodWithClass> loadHandlers(ApplicationContext ctx) {
        Map<String, MethodWithClass> handlers = new HashMap<>();

        Map<String, Object> beans = ctx.getBeansWithAnnotation(TebotController.class);

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Class clazz = entry.getValue().getClass();
            Method[] methods = clazz.getDeclaredMethods();

            for (Method method : methods) {
                for (TebotMapping a : method.getAnnotationsByType(TebotMapping.class)) {

                    Class<?> paramClass = Arrays.stream(method.getParameterTypes())
                                                .findFirst()
                                                .orElse(Void.class);

                    ArgsConverter converter = oneArgsConverters.get(paramClass);

                    if (converter == null) {
                        logger.error("Cannot find args converter for type {}", paramClass);
                        continue;
                    }

                    ResultProcessor resultProcessor = resultProcessors.get(method.getReturnType());

                    if (resultProcessor == null) {
                        logger.error("Cannot find resilt processor for type {0}", method.getReturnType());
                        continue;
                    }

                    MethodWithClass mc = new MethodWithClass(method, entry.getValue());
                    mc.setArgsConverter(converter);
                    mc.setResultProcessor(resultProcessor);

                    handlers.put(a.value(), mc);

                    logger.info("added tebot handler \"{}\" - {}", a.value(), method);
                }
            }
        }

        return handlers;
    }

    public interface ArgsConverter {
        Object[] convert(Update update);
    }

    private static Map<Class<?>, ArgsConverter> oneArgsConverters = new HashMap<>();
    static {
        oneArgsConverters.put(String.class, new MessageArgsConverter());
        oneArgsConverters.put(Update.class, new SimpleArgsConverter());
        oneArgsConverters.put(Void.class, new VoidArgsConverter());
    }

    private static Map<Class<?>, ResultProcessor> resultProcessors;
    private static Map<Class<?>, ResultProcessor> createResultProcessors(TelegramClient client) {
        Map<Class<?>, ResultProcessor> resultProcessors = new HashMap<>();

        resultProcessors.put(String.class, new StringResultProcessor(client));
        resultProcessors.put(SendMessageRequest.class, new MessageResultProcessor(client));
        resultProcessors.put(Void.class, new NoOpResultProcessor());

        return resultProcessors;
    }

    public static class VoidArgsConverter implements ArgsConverter {

        @Override
        public Object[] convert(Update update) {
            return new Object[0];
        }
    }

    public static class SimpleArgsConverter implements ArgsConverter {

        @Override
        public Object[] convert(Update update) {
            return new Object[] { update };
        }
    }

    public static class MessageArgsConverter implements ArgsConverter {

        @Override
        public Object[] convert(Update update) {
            return new Object[] { update.getMessage() };
        }
    }

    public interface ResultProcessor {
        void process(Object obj, Update update);
    }

    public static class NoOpResultProcessor implements ResultProcessor {

        @Override
        public void process(Object obj, Update update) {
        }
    }

    public static class MessageResultProcessor implements ResultProcessor {

        private TelegramClient client;

        public MessageResultProcessor(TelegramClient client) {
            this.client = client;
        }

        @Override
        public void process(Object obj, Update update) {
            try {
                Message result = client.sendMessage((SendMessageRequest) obj);
                logger.debug("Send message {}", result);
            } catch (IOException ex) {
                logger.error("Error sending message " + obj, ex);
            }
        }
    }

    public static class StringResultProcessor extends MessageResultProcessor implements ResultProcessor {

        public StringResultProcessor(TelegramClient client) {
            super(client);
        }

        @Override
        public void process(Object obj, Update update) {
            SendMessageRequest request = new SendMessageRequest();

            request.setChatId(Integer.toString(update.getMessage().getChat().getId()));
            request.setReplyToMessageId(update.getMessage().getId());
            request.setText((String)obj);

            super.process(request, update);
        }
    }

    public interface MethodInvoker {
        void invoke(Update update);
    }

    public static class InvokerImpl implements MethodInvoker {

        private Object[] args;

        @Override
        public void invoke(Update update) {

        }
    }

    public static void main ( String[] args ) throws Exception
    {
        //System.out.println( "Hello World!" );
        logger.debug("Hello, world!");

        TelegramClient client = new HttpTelegramClient("Api key here");

        resultProcessors = createResultProcessors(client);

        ApplicationContext ctx = new AnnotationConfigApplicationContext(App.class);
        mappings = loadHandlers(ctx);

        HelloController ctrl = ctx.getBean(HelloController.class);
        System.out.println("say = " + ctrl.hello());

        for (Map.Entry<String, MethodWithClass> entry : mappings.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue().invoke());
        }

        System.out.println("end");


        ExecutorService executorService = Executors.newFixedThreadPool(2);

        int lastOffset = -1;

        while (true) {
            GetUpdatesRequest updateParams = new GetUpdatesRequest();
            updateParams.setOffset(lastOffset + 1);

            List<Update> updates = client.getUpdates(updateParams);

            for (Update update : updates) {
                executorService.submit(() -> handleRequest(update));
                lastOffset = Math.max(lastOffset, update.getId());
            }
        }

//        executorService.shutdownNow();
//        return;

/*
        TelegramClient client = new HttpTelegramClient("Api key here");

        User result = client.getMe();
        System.out.println(result);
*/


/*
        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setChatId("85226090");
        sendMessageRequest.setText("Hello from bot");
        Message sendMessageResponse = client.sendMessage(sendMessageRequest);

        System.out.println(sendMessageRequest);
        System.out.println(sendMessageResponse);
*/
/*
        List<Update> updates = client.getUpdates();

        for (Update update : updates) {
            System.out.println(update);
        }
*/
    }
}
