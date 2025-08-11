package com.example.homework_1.hw03;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BankingSimulator {
    private static final int numberOfAccounts = 200;
    private static final long min_balance = 0L;
    private static final long max_balance = 1000L;
    private static final int numberOfThreads = 1000;
    private static final int TRANSFERS_PER_THREAD = 100;

    public static void main(String[] args) {
        System.out.println("-- Banking Simulator with Thread Safety --\n");


        // Test synchronized version
        System.out.println("\n1. Testing synchronized version:");
        testBankSynchronized();

        // Test ReentrantLock version
        System.out.println("\n2. Testing REENTRANT lock version:");
        testBankWithLocks();

        // Test Atomic version
        System.out.println("\n3. Testing Atomic version:");
        testBankAtomic();

        System.out.println("\n4. Testing Unsafe version:");
        testBankUnsafe();
    }

    private static void testBankSynchronized() {
        BankSynchronized bank = new BankSynchronized(numberOfAccounts, min_balance, max_balance);
        BigInteger initialTotal = bank.getAllBalanceOfAccounts();
        System.out.println("Initial total: " + initialTotal);

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        Random random = new Random();

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < TRANSFERS_PER_THREAD; j++) {
                    int from = bank.pickRandomAccount();
                    int to = bank.pickRandomAccount();
                    if (from != to) {
                        long fromBalance = bank.getAccountBalance(from);
                        if (fromBalance > 0) {
                            long transferAmount = random.nextLong(fromBalance) + 1;
                            bank.transfer(from, to, transferAmount);
                        }
                    }
                }
            });
        }

        shutdownAndAwaitTermination(executor);

        BigInteger finalTotal = bank.getAllBalanceOfAccounts();
        System.out.println("Final total: " + finalTotal);
        System.out.println("Total preserved: " + initialTotal.equals(finalTotal));
        System.out.println("Difference: " + finalTotal.subtract(initialTotal));
    }

    private static void testBankWithLocks() {
        BankWithLocks bank = new BankWithLocks(numberOfAccounts, min_balance, max_balance);
        BigInteger initialTotal = bank.getAllBalanceOfAccounts();
        System.out.println("Initial total: " + initialTotal);

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        Random random = new Random();

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < TRANSFERS_PER_THREAD; j++) {
                    int from = bank.pickRandomAccount();
                    int to = bank.pickRandomAccount();
                    if (from != to) {
                        long fromBalance = bank.getAccountBalance(from);
                        if (fromBalance > 0) {
                            long transferAmount = random.nextLong(fromBalance) + 1;
                            bank.transfer(from, to, transferAmount);
                        }
                    }
                }
            });
        }

        shutdownAndAwaitTermination(executor);

        BigInteger finalTotal = bank.getAllBalanceOfAccounts();
        System.out.println("Final total: " + finalTotal);
        System.out.println("Total preserved: " + initialTotal.equals(finalTotal));
        System.out.println("Difference: " + finalTotal.subtract(initialTotal));
    }

    private static void testBankAtomic() {
        BankAtomic bank = new BankAtomic(numberOfAccounts,min_balance, max_balance);
        BigInteger initialTotal = bank.getAllBalanceOfAccounts();
        System.out.println("Initial total: " + initialTotal);

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        Random random = new Random();

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < TRANSFERS_PER_THREAD; j++) {
                    int from = bank.pickRandomAccount();
                    int to = bank.pickRandomAccount();
                    if (from != to) {
                        long fromBalance = bank.getAccountBalance(from);
                        if (fromBalance > 0) {
                            long transferAmount = random.nextLong(fromBalance) + 1;
                            bank.transfer(from, to, transferAmount);
                        }
                    }
                }
            });
        }

        shutdownAndAwaitTermination(executor);

        BigInteger finalTotal = bank.getAllBalanceOfAccounts();
        System.out.println("Final total: " + finalTotal);
        System.out.println("Total preserved: " + initialTotal.equals(finalTotal));
        System.out.println("Difference: " + finalTotal.subtract(initialTotal));
    }

    private static void testBankUnsafe() {
        BankUnsafe bank = new BankUnsafe(numberOfAccounts, min_balance, max_balance);
        BigInteger initialTotal = bank.getSumOfAllAccounts();
        System.out.println("Initial total: " + initialTotal);

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        Random random = new Random();

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < TRANSFERS_PER_THREAD; j++) {
                    int from = bank.pickRandomAccountId();
                    int to = bank.pickRandomAccountId();
                    if (from != to) {
                        long fromBalance = bank.getAccountBalance(from);
                        if (fromBalance > 0) {
                            long transferAmount = random.nextLong(fromBalance) + 1;

                            // Non-atomic operations
                            long newFromBalance = bank.getAccountBalance(from) - transferAmount;
                            bank.setAccountBalance(from, newFromBalance);

                            long newToBalance = bank.getAccountBalance(to) + transferAmount;
                            bank.setAccountBalance(to, newToBalance);
                        }
                    }
                }
            });
        }

        shutdownAndAwaitTermination(executor);

        BigInteger finalTotal = bank.getSumOfAllAccounts();
        System.out.println("Final total: " + finalTotal);
        System.out.println("Total preserved: " + initialTotal.equals(finalTotal));
        System.out.println("Difference: " + finalTotal.subtract(initialTotal));
    }

    private static void shutdownAndAwaitTermination(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
