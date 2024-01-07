package com.hexagonali.appinsights;

class TestSetup implements ITestSetup {
    private final TestMode testMode;
    private final TestParameters testParameters;

    TestSetup(TestMode testMode, TestParameters testParameters) {
        this.testMode = testMode;
        this.testParameters = testParameters;
    }

    @Override
    public int getMaxMessageLength() {
        switch (testMode) {
            case SINGLE_THREADED_SHORT_MESSAGES, MULTI_THREADED_SHORT_MESSAGES -> {
                return testParameters.getShortMessagesLength();
            }

            case SINGLE_THREADED_LONG_MESSAGES, MULTI_THREADED_LONG_MESSAGES -> {
                return testParameters.getLongMessagesMaxLength();
            }

            default -> throw new RuntimeException("Unexpected test mode: " + testMode);
        }
    }

    @Override
    public int getNumberOfThreads() {
        switch (testMode) {
            case SINGLE_THREADED_SHORT_MESSAGES, SINGLE_THREADED_LONG_MESSAGES -> { return 1; }

            case MULTI_THREADED_SHORT_MESSAGES, MULTI_THREADED_LONG_MESSAGES -> {
                return testParameters.getMaxNumberOfThreads();
            }

            default -> throw new RuntimeException("Unexpected test mode: " + testMode);
        }
    }

    @Override
    public int getMessagesPerThread() {
        switch (testMode) {
            case SINGLE_THREADED_SHORT_MESSAGES, SINGLE_THREADED_LONG_MESSAGES -> {
                return testParameters.getTotalNumberOfMessages();
            }

            case MULTI_THREADED_SHORT_MESSAGES, MULTI_THREADED_LONG_MESSAGES -> {
                return testParameters.getTotalNumberOfMessages() / testParameters.getMaxNumberOfThreads();
            }

            default -> throw new RuntimeException("Unexpected test mode: " + testMode);
        }
    }

    @Override
    public int getMaxWaitTimeSeconds() {
        return testParameters.getMaxWaitTimeSeconds();
    }

    @Override
    public boolean isCipherMessages() {
        return testParameters.isCipherMessages();
    }

    @Override
    public String toString() {
        switch (testMode) {
            case SINGLE_THREADED_SHORT_MESSAGES -> {
                return "Single-threaded with short messages (maximum message length: " +
                        testParameters.getShortMessagesLength() + ")";
            }

            case SINGLE_THREADED_LONG_MESSAGES -> {
                return "Single-threaded with long messages (maximum message length: " +
                        testParameters.getLongMessagesMaxLength() + ")";
            }

            case MULTI_THREADED_SHORT_MESSAGES -> {
                return "Multi-threaded (number of threads: " + testParameters.getMaxNumberOfThreads() +
                        ") with short messages (maximum message length: " +
                        testParameters.getShortMessagesLength() + ")";
            }

            case MULTI_THREADED_LONG_MESSAGES -> {
                return "Multi-threaded (number of threads: " + testParameters.getMaxNumberOfThreads() +
                        ") with long messages (maximum message length: " +
                        testParameters.getLongMessagesMaxLength() + ")";
            }

            default -> throw new RuntimeException("Unexpected test mode: " + testMode);
        }
    }
}
