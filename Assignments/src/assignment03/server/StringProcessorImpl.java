package assignment03.server;

import java.util.Date;
import java.util.Objects;

public class StringProcessorImpl implements StringProcessor {

    public static final String TIME_STRING = "time";

    private static volatile StringProcessorImpl instance;

    private StringProcessorImpl() {
    }

    public synchronized static StringProcessorImpl getInstance() {

        if (Objects.isNull(instance)) {
            instance = new StringProcessorImpl();
        }

        return instance;
    }

    @Override
    public String process(String message) {

        if (Objects.isNull(message) || message.trim().isEmpty()) {
            return RpcRequest.EMPTY_STRING;
        }

        if (TIME_STRING.equalsIgnoreCase(message.trim())) {
            return new Date().toString();
        } else {
            return message.toUpperCase();
        }
    }
}