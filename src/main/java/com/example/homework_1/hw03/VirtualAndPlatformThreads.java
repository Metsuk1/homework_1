package com.example.homework_1.hw03;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualAndPlatformThreads {
    private static int cnt_thread = 8000;
    private static int sleep_time = 200;

    public static void main(String[] args) {
        System.out.println("=== Thread Performance Comparison ===");
        System.out.println("Thread count: " + cnt_thread);
        System.out.println("Sleep duration: " + sleep_time + " ms\n");

        if (isVirtualThreadsSupported()) {
            System.out.println("\n--- Testing Virtual Threads--- ");
            testVirtualThread();
        } else {
            System.out.println("\n Virtual Threads not supported ");
        }

        System.out.println("\n--- Testing Platform Threads ---");
        testPlatformThread();

        demonstrateThreadLimitations();

    }

    private static boolean isVirtualThreadsSupported() {
        try {
            Executors.class.getMethod("newVirtualThreadPerTaskExecutor");

            return true;
        } catch (NoSuchMethodException e) {

            return false;
        }
    }

    private static void testVirtualThread() {
        Runtime runtime = Runtime.getRuntime();

        System.gc();
        long startMemory = runtime.totalMemory() - runtime.freeMemory();

        Instant startTime = Instant.now();

        try(ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()){
            CountDownLatch countDownLatch = new CountDownLatch(cnt_thread);

            for (int i = 0; i < cnt_thread; i++) {
                executor.submit(() -> {
                    try{
                        Thread.sleep(sleep_time);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }finally {
                        countDownLatch.countDown();
                    }
                });
            }

            try{
                countDownLatch.await();
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        Instant endTime = Instant.now();
        long duration = Duration.between(startTime, endTime).toMillis();

        System.gc();
        long endMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = endMemory - startMemory;

        System.out.println("Virtual Threads Results:");
        System.out.println("Time taken: " + duration + " ms");
        System.out.println("Memory used: " + formatBytes(memoryUsed));
        System.out.println("Memory per thread: " + formatBytes(memoryUsed / cnt_thread));
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    private static void testPlatformThread() {
        Runtime runtime = Runtime.getRuntime();

        int reducedThreadCount = Math.min(cnt_thread, 1000);
        System.out.println("Testing with reduced count: " + reducedThreadCount + " threads");

        System.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        Instant start = Instant.now();

        List<Thread> threads = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(reducedThreadCount);

        try {
            // Create and start platform threads
            for (int i = 0; i < reducedThreadCount; i++) {
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(sleep_time);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        latch.countDown();
                    }
                });
                thread.start();
                threads.add(thread);
            }

            // Wait for all threads to complete
            latch.await();

            // Wait for all threads to finish
            for (Thread thread : threads) {
                thread.join();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (OutOfMemoryError e) {
            System.err.println("OutOfMemoryError: Cannot create " + reducedThreadCount + " platform threads");
            System.err.println("Current heap size: " + formatBytes(runtime.maxMemory()));
            System.err.println("Try running with: java -Xmx20g -Xss256k ThreadComparison");
            return;
        }

        Instant endTime = Instant.now();
        long duration = Duration.between(start, endTime).toMillis();

        System.gc();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = memoryAfter - memoryBefore;

        System.out.println("Platform Threads Results (" + reducedThreadCount + " threads):");
        System.out.println("  Time taken: " + duration + " ms");
        System.out.println("  Memory used: " + formatBytes(memoryUsed));
        System.out.println("  Memory per thread: " + formatBytes(memoryUsed / reducedThreadCount));

        if (reducedThreadCount < cnt_thread) {
            long estimatedMemory = (memoryUsed / reducedThreadCount) * cnt_thread;
            System.out.println("\nEstimated for " + cnt_thread + " platform threads:");
            System.out.println("  Estimated memory needed: " + formatBytes(estimatedMemory));
        }

    }

    private static void demonstrateThreadLimitations() {
        System.out.println("\n1.Problems with 8000 platform threads\n");

        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        System.out.println("Current state of JVM:");
        System.out.println("  max memory (heap): " + formatBytes(maxMemory));
        System.out.println("  Total allocated memory: " + formatBytes(totalMemory));
        System.out.println("  free memory " + formatBytes(freeMemory));
        System.out.println("  used memoty " + formatBytes(totalMemory - freeMemory));

        System.out.println("\nCompute memory for PlatformThreads");

        long defaultStackSize = 1024 * 1024; // 1MB по умолчанию в большинстве JVM
        System.out.println("  size of stack for 1 thread " + formatBytes(defaultStackSize));

        long memoryFor8000Threads = 8000 * defaultStackSize;
        System.out.println("  Memory for 8000 threads " + formatBytes(memoryFor8000Threads));

        // Сравнение с доступной памятью
        if (memoryFor8000Threads > maxMemory) {
            System.out.println(" Problems we need " + formatBytes(memoryFor8000Threads) +
                    ", but available is  " + formatBytes(maxMemory));
            System.out.println("  Problem is OutOfMemoryError!");
        } else {
            System.out.println("  memory complete");
        }
    }
}
