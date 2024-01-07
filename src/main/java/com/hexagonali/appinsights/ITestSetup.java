package com.hexagonali.appinsights;

interface ITestSetup {
    int getMaxMessageLength();
    int getNumberOfThreads();
    int getMessagesPerThread();
    int getMaxWaitTimeSeconds();
    boolean isCipherMessages();
}
