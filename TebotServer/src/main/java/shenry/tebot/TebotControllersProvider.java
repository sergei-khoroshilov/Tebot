package shenry.tebot;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import shenry.tebot.annotation.TebotController;
import shenry.tebot.annotation.TebotMapping;
import shenry.tebot.argconverter.ArgConverter;
import shenry.tebot.argconverter.ArgConverterFactory;
import shenry.tebot.resultprocessor.ResultProcessor;
import shenry.tebot.resultprocessor.ResultProcessorFactory;

import java.lang.reflect.Method;
import java.util.Map;

@Component
public class TebotControllersProvider {

    private static final Logger logger = LoggerFactory.getLogger(TebotControllersProvider.class);

    private final ArgConverterFactory argConverterFactory;
    private final ResultProcessorFactory resultProcessorFactory;
    private final ApplicationContext applicationContext;

    @Autowired
    public TebotControllersProvider(ArgConverterFactory argConverterFactory,
                                    ResultProcessorFactory resultProcessorFactory,
                                    ApplicationContext applicationContext) {
        this.argConverterFactory = argConverterFactory;
        this.resultProcessorFactory = resultProcessorFactory;
        this.applicationContext = applicationContext;
    }

    public Multimap<String, UpdateMethodInvoker> loadHandlers() {
        Multimap<String, UpdateMethodInvoker> handlers = ArrayListMultimap.create();

        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(TebotController.class);

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
