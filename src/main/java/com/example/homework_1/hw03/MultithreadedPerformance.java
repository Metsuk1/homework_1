package com.example.homework_1.hw03;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class MultithreadedPerformance {
    private static int array_size = 100_000_000;
    private static short[] array;

    public static void main(String[] args) {
        array = new short[array_size];

        System.out.println("Generating array...");
        for (int i = 0; i < array_size; i++) {
            array[i] = (short) (i % 1000);
        }
        System.out.println("Array generated successfully");

        int[] threadCounts = {1,10,100,1000};

        try(FileWriter writer = new FileWriter("performance_test.txt")) {
            writer.write("Thread Count\tMethod\tTime (ms)\n");

            for(int threadCount : threadCounts) {
                System.out.println("\nTesting with " + threadCount + " threads:");

                // Test parallel stream
                long startTime = System.nanoTime();
                long sumParallelStream = sumWithParallelStream(threadCount);
                long endTime = System.nanoTime();
                long elapsedMs = (endTime - startTime) / 1_000_000;


                System.out.println("parallel Stream: " + elapsedMs + " ms, summ: " + sumParallelStream);
                writer.write(threadCount + "\t\tParallel Stream\t" + elapsedMs + "\n");

                // Test manual threading
                startTime = System.nanoTime();
                long sumParallelThreads = sumWithParallelThreads(threadCount);
                endTime = System.nanoTime();
                elapsedMs = (endTime - startTime) / 1_000_000;

                System.out.println("Manual Threads: " + elapsedMs + " ms, Sum: " + sumParallelThreads);
                writer.write(threadCount + "\t\tManual Threads\t" + elapsedMs + "\n");

                //check both methods should produce same result
                if (sumParallelStream != sumParallelThreads) {
                    System.err.println("ERROR: Sums don't match!");
                }
            }

        } catch (IOException e) {
            System.out.println("error write to file " + e.getMessage());
        }

    }

    private static long sumWithParallelStream(int threadCount) {
        var forkJoinPool = new java.util.concurrent.ForkJoinPool(threadCount);

        try {
            return forkJoinPool.submit(() ->
                    java.util.stream.IntStream.range(0, array.length)
                            .parallel()
                            .mapToLong(i -> array[i])
                            .sum()
            ).get();
        } catch (Exception e) {
            throw new RuntimeException("Error in parallel stream computation", e);
        } finally {
            forkJoinPool.shutdown();
        }
    }

    private static long sumWithParallelThreads(int threadCount) {
        Thread[] threads = new Thread[threadCount];
        long[] partialSums = new long[threadCount];
        int chunkSize = array.length / threadCount;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;

            threads[i] = new Thread(() -> {
                int start = index * chunkSize;
                int end = (index == threadCount - 1) ? array.length : start + chunkSize;
                long sum = 0;
                for (int j = start; j < end; j++) {
                    sum += array[j];
                }
                partialSums[index] = sum;
                latch.countDown();
            });
            threads[i].start();
        }

        try {
            latch.await(); // ждемм завершения всех тредов
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread interrupted", e);
        }

        long totalSum = 0;
        for (long part : partialSums) {
            totalSum += part;
        }

        return totalSum;
    }
}
