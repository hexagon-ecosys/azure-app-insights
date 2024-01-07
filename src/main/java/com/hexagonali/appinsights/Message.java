package com.hexagonali.appinsights;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

class Message {
    private final static Logger LOGGER = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    private static final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final Random generator;
    private MessageType messageType;
    private String message;
    private final Map<MessageType, AtomicInteger> summarizer;
    private final int maxMessageLength;
    private final boolean cipherMessages;
    public static final String LOCK = "LOCK";

    private MessageType includeCipherMessageType() {
        double x = generator.nextDouble();
        if (x <= 0.2) {
            return MessageType.SECURE;
        }
        else if (x <= 0.4) {
            return MessageType.WARN;
        }
        else {
            return MessageType.INFO;
        }
    }

    private MessageType excludeCipherMessageType() {
        double x = generator.nextDouble();
        if (x <= 0.3) {
            return MessageType.WARN;
        }
        else {
            return MessageType.INFO;
        }
    }

    private MessageType getMessageType() {
        return cipherMessages ? includeCipherMessageType(): excludeCipherMessageType();
    }

    private void setType() {
        messageType = getMessageType();
    }

    private int getNonZeroRandomValue() {
        return getNonZeroBoundedRandomValue(-1);
    }

    private int getNonZeroBoundedRandomValue(int bound) {
        int value;
        do {
            value = bound<0 ? generator.nextInt(): generator.nextInt(bound);
        }
        while (value == 0);

        return value;
    }

    private void setMessage() {
        StringBuilder sb = new StringBuilder();

        // generate a random character and append it to the message string
        for (int i = 0; i < getNonZeroBoundedRandomValue(maxMessageLength); i++) {
            sb.append(characters.charAt(generator.nextInt(characters.length())));
        }

        if (messageType == MessageType.SECURE) {
            message = Encryptor.createCipherText(sb.toString(), "passphrase");
        }
        else {
            message = sb.toString();
        }
    }

    private void updateStats() {
        AtomicInteger cnt = summarizer.get(messageType);
        if (cnt == null) {
            synchronized (LOCK) {
                cnt = summarizer.get(messageType);
                if (cnt == null) {
                    summarizer.put(messageType, new AtomicInteger(1));
                }
                else {
                    cnt.getAndIncrement();
                }
            }
        }
        else {
            cnt.getAndIncrement();
        }
    }

    Message(Random generator, Map<MessageType, AtomicInteger> summarizer, int maxMessageLength, boolean cipherMessages) {
        this.generator = generator;
        this.summarizer = summarizer;
        this.maxMessageLength = maxMessageLength;
        this.cipherMessages = cipherMessages;

        setType();
        setMessage();
        updateStats();
    }

    void log() {
        switch (messageType) {
            case INFO -> LOGGER.info(message);
            case WARN -> LOGGER.warn(message);
            case SECURE -> LOGGER.info(message);
            default -> throw new RuntimeException("Unexpected message type: " + messageType);
        }
    }
}
