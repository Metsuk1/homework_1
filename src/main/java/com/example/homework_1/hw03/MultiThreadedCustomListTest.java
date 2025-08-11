package com.example.homework_1.hw03;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MultiThreadedCustomListTest {
    private static final int ELEMENTS_PER_THREAD = 1_000_000;
    private static final int NUM_THREADS = 2;
    private static final int CORRECTNESS_TEST_RUNS = 100;

    public static void main(String[] args) {
        System.out.println("=== Multithreaded CustomList Testing ===\n");

        // Correctness tests
        System.out.println("CORRECTNESS TESTS:");
        System.out.println("Expected elements after test: " + (ELEMENTS_PER_THREAD * NUM_THREADS));
        System.out.println();

        runCorrectnessTests("1. Basic CustomList (NOT thread-safe)", CustomList::new);
        runCorrectnessTests("2. Synchronized CustomList", () -> new SynchronizedCustomList<>(new CustomList<>()));
        runCorrectnessTests("3. ReadWriteLock CustomList", () -> new ReadWriteLockCustomList<>(new CustomList<>()));

        System.out.println("\n" + "=".repeat(60));

        // Performance tests
        System.out.println("\nPERFORMANCE TESTS:");
        runPerformanceTest("1. Basic CustomList (NOT thread-safe)", CustomList::new);
        runPerformanceTest("2. Synchronized CustomList", () -> new SynchronizedCustomList<>(new CustomList<>()));
        runPerformanceTest("3. ReadWriteLock CustomList", () -> new ReadWriteLockCustomList<>(new CustomList<>()));
    }

    @FunctionalInterface
    interface ListFactory<T> {
        CustomList<T> create();
    }

    private static void runCorrectnessTests(String testName, ListFactory<Integer> factory) {
        System.out.println(testName + ":");

        List<Integer> results = new ArrayList<>();
        int minSize = Integer.MAX_VALUE;
        int maxSize = 0;

        for (int run = 0; run < CORRECTNESS_TEST_RUNS; run++) {
            CustomList<Integer> list = factory.create();

            try {
                CountDownLatch latch = new CountDownLatch(NUM_THREADS);

                // Create threads that add elements
                for (int t = 0; t < NUM_THREADS; t++) {
                    final int threadId = t;
                    Thread.ofVirtual().start(() -> {
                        try {
                            for (int i = 0; i < ELEMENTS_PER_THREAD; i++) {
                                list.add(threadId * ELEMENTS_PER_THREAD + i);
                            }
                        } finally {
                            latch.countDown();
                        }
                    });
                }

                // Wait for all threads to complete
                latch.await();

                int size = list.size();
                results.add(size);
                minSize = Math.min(minSize, size);
                maxSize = Math.max(maxSize, size);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Test interrupted");
                return;
            }
        }

        // Calculate statistics
        double average = results.stream().mapToInt(Integer::intValue).average().orElse(0);
        int expectedSize = ELEMENTS_PER_THREAD * NUM_THREADS;

        System.out.println("  Runs: " + CORRECTNESS_TEST_RUNS);
        System.out.println("  Expected size: " + expectedSize);
        System.out.println("  Average size: " + String.format("%.2f", average));
        System.out.println("  Min size: " + minSize);
        System.out.println("  Max size: " + maxSize);
        System.out.println("  Size range: " + (maxSize - minSize));
        System.out.println("  Data loss: " + String.format("%.2f%%",
                100.0 * (expectedSize - average) / expectedSize));
        System.out.println();
    }

    private static void runPerformanceTest(String testName, ListFactory<Integer> factory) {
        System.out.println(testName + ":");

        // Warm up
        for (int i = 0; i < 3; i++) {
            performSinglePerformanceTest(factory);
        }

        // Actual test
        long totalTime = 0;
        int runs = 5;

        for (int run = 0; run < runs; run++) {
            long time = performSinglePerformanceTest(factory);
            totalTime += time;
        }

        double averageTime = totalTime / (double) runs;
        double throughput = (ELEMENTS_PER_THREAD * NUM_THREADS) / (averageTime / 1000.0);

        System.out.println("  Average time: " + String.format("%.2f", averageTime) + " ms");
        System.out.println("  Throughput: " + String.format("%.0f", throughput) + " elements/second");
        System.out.println();
    }

    private static long performSinglePerformanceTest(ListFactory<Integer> factory) {
        CustomList<Integer> list = factory.create();

        long startTime = System.currentTimeMillis();

        try {
            CountDownLatch latch = new CountDownLatch(NUM_THREADS);

            for (int t = 0; t < NUM_THREADS; t++) {
                final int threadId = t;
                Thread.ofVirtual().start(() -> {
                    try {
                        for (int i = 0; i < ELEMENTS_PER_THREAD; i++) {
                            list.add(threadId * ELEMENTS_PER_THREAD + i);
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }

        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}
