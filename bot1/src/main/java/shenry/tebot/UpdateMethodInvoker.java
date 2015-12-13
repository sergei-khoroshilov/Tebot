package shenry.tebot;

import shenry.tebot.argconverter.ArgConverter;
import shenry.tebot.resultprocessor.ResultProcessor;
import shenry.tebot.telegramclient.types.Update;

import java.lang.reflect.Method;

public class UpdateMethodInvoker {
    private String command;
    private Method method;
    private Object obj;
    private ArgConverter[] argConverters;
    private ResultProcessor resultProcessor;

    public UpdateMethodInvoker(String command, Method method, Object obj, ArgConverter[] argConverters, ResultProcessor resultProcessor) {
        this.command = command;
        this.method = method;
        this.obj = obj;
        this.argConverters = argConverters;
        this.resultProcessor = resultProcessor;
    }

    public String getCommand() {
        return command;
    }

    public void invoke(Update update) throws Exception {
        Object[] args = convertArgs(update);
        Object result = method.invoke(obj, args);
        resultProcessor.process(result, update);
    }

    private Object[] convertArgs(Update update) {
        Object[] args = new Object[argConverters.length];

        for (int i = 0; i < argConverters.length; i++) {
            args[i] = argConverters[i].convert(command, update);
        }

        return args;
    }
}
