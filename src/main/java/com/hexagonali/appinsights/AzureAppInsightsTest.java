package com.hexagonali.appinsights;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AzureAppInsightsTest {
    private static LocalDateTime started;

    private static Runnable process(ITestSetup testSetup, Map<MessageType, AtomicInteger> summarizer) {
        return () -> {
            final Random generator = new Random();

            for (int i=0; i<testSetup.getMessagesPerThread(); i++) {
                new Message(generator, summarizer, testSetup.getMaxMessageLength(), testSetup.isCipherMessages()).log();
            }
        };
    }

    private static void summarize(ITestSetup testSetup, Map<MessageType, AtomicInteger> summarizer) {
        LocalDateTime ended = LocalDateTime.now();

        StringBuilder sb = new StringBuilder(System.lineSeparator());
        sb.append("--------------------------------------------------------------------------------------------------");
        sb.append(System.lineSeparator());
        sb.append(testSetup);
        sb.append(System.lineSeparator());
        sb.append("--------------------------------------------------------------------------------------------------");
        sb.append(System.lineSeparator());

        int accumulator = 0;

        for (Map.Entry<MessageType, AtomicInteger> me: summarizer.entrySet()) {
            sb.append(me.getKey());
            sb.append(" \t");
            int value = me.getValue().get();
            sb.append(value);
            sb.append(System.lineSeparator());

            accumulator += value;
        }

        sb.append("--------------");
        sb.append(System.lineSeparator());
        sb.append("TOTAL \t");
        sb.append(accumulator);
        sb.append(System.lineSeparator());
        sb.append("--------------");
        sb.append(System.lineSeparator());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        sb.append(System.lineSeparator());
        sb.append("Run time: \t");
        sb.append(Duration.between(started, ended));
        sb.append(System.lineSeparator());
        sb.append("Started: \t");
        sb.append(formatter.format(started));
        sb.append(System.lineSeparator());
        sb.append("Completed: \t");
        sb.append(formatter.format(LocalDateTime.now()));
        sb.append(System.lineSeparator());

        sb.append("--------------------------------------------------------------------------------------------------");
        sb.append(System.lineSeparator());

        System.out.print(sb);
    }

    private static ITestSetup setup(String[] args) {
        int selection = 0;
        if (args != null && args.length != 0) {
            try {
                selection = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e) {
                System.err.println("If supplying a parameter to this program, it must be within [0,3]");
                System.err.println("0: Single-threaded with short messages");
                System.err.println("1: Single-threaded with long messages");
                System.err.println("2: Multi-threaded with short messages");
                System.err.println("3: Multi-threaded with long messages");
                System.exit(1);
            }
        }

        switch (selection) {
            case 0 -> {
                return new TestSetup(TestMode.SINGLE_THREADED_SHORT_MESSAGES, new TestParameters());
            }

            case 1 -> {
                return new TestSetup(TestMode.SINGLE_THREADED_LONG_MESSAGES, new TestParameters());
            }

            case 2 -> {
                return new TestSetup(TestMode.MULTI_THREADED_SHORT_MESSAGES, new TestParameters());
            }

            case 3 -> {
                return new TestSetup(TestMode.MULTI_THREADED_LONG_MESSAGES, new TestParameters());
            }

            default -> throw new RuntimeException("Unrecognized test setup selection = " + selection);
        }
    }

    private static void runSingleThreaded(ITestSetup testSetup) {
        Map<MessageType, AtomicInteger> summarizer = new HashMap<>();
        process(testSetup, summarizer).run();
        summarize(testSetup, summarizer);
    }

    private static void runMultiThreaded(ITestSetup testSetup) {
        Map<MessageType, AtomicInteger> summarizer = new ConcurrentHashMap<>();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(testSetup.getNumberOfThreads());

        for (int i = 0; i < testSetup.getNumberOfThreads(); i++) {
            executor.submit(process(testSetup, summarizer));
        }

        executor.shutdown();
        try {
            if (executor.awaitTermination(testSetup.getMaxWaitTimeSeconds(), TimeUnit.SECONDS)) {
                summarize(testSetup, summarizer);
            }
        } catch (InterruptedException ex) {
            System.err.println("Interrupted while awaiting completion: " + ex.getMessage());
            Thread.currentThread().interrupt(); // preserve interrupt status
        } finally {
            List<Runnable> awaiting = executor.shutdownNow();
            if (!awaiting.isEmpty()) {
                System.err.println(awaiting.size() + " tasks were still awaiting execution");
                System.exit(2);
            }
        }
    }

    public static void main(String[] args) {
        started = LocalDateTime.now();

        ITestSetup testSetup = setup(args);

        if (testSetup.getNumberOfThreads() == 1) {
            runSingleThreaded(testSetup);
        }
        else {
            runMultiThreaded(testSetup);
        }
    }
}
