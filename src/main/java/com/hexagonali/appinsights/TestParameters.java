package com.hexagonali.appinsights;

class TestParameters {
    private final int shortMessagesLength;
    private final int longMessagesMaxLength;
    private final int maxNumberOfThreads;
    private final int totalNumberOfMessages;
    private final int maxWaitTimeSeconds;
    private final boolean cipherMessages;

    TestParameters() {
        shortMessagesLength = 500;
        longMessagesMaxLength = 20000;
        maxNumberOfThreads = 20;
        totalNumberOfMessages = 20000;
        maxWaitTimeSeconds = 120;
        cipherMessages = false;
    }

    TestParameters(int shortMessagesMaxLength, int longMessagesMaxLength, int maxNumberOfThreads,
                   int totalNumberOfMessages, int maxWaitTimeSeconds, boolean cipherMessages) {
        this.shortMessagesLength = shortMessagesMaxLength;
        this.longMessagesMaxLength = longMessagesMaxLength;
        this.maxNumberOfThreads = maxNumberOfThreads;
        this.totalNumberOfMessages = totalNumberOfMessages;
        this.maxWaitTimeSeconds = maxWaitTimeSeconds;
        this.cipherMessages = cipherMessages;
    }

    int getShortMessagesLength() {
        return shortMessagesLength;
    }

    int getLongMessagesMaxLength() {
        return longMessagesMaxLength;
    }

    int getMaxNumberOfThreads() {
        return maxNumberOfThreads;
    }

    int getTotalNumberOfMessages() {
        return totalNumberOfMessages;
    }

    public int getMaxWaitTimeSeconds() {
        return maxWaitTimeSeconds;
    }

    public boolean isCipherMessages() {
        return cipherMessages;
    }
}
