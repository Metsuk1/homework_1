package com.example.homework_1.hw03;

import com.example.homework_1.hw03.tests.TestDeadlocks;
import com.example.homework_1.hw03.types_of_banks.BankAtomic;
import com.example.homework_1.hw03.types_of_banks.BankSynchronized;
import com.example.homework_1.hw03.types_of_banks.BankUnsafe;
import com.example.homework_1.hw03.types_of_banks.BankWithLocks;

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

        System.out.println("\n" + "=".repeat(60));
        System.out.println("DEADLOCK DEMONSTRATIONS");
        System.out.println("=".repeat(60));

        System.out.println("\nDEADLOCK DEMO 1: Unordered Lock Acquisition");
        demonstrateDeadlock1();

        System.out.println("\nDEADLOCK DEMO 2: Nested Synchronized Blocks");
        demonstrateDeadlock2();

        System.out.println("\nDEADLOCK DEMO 3: Circular Waiting Chain");
        demonstrateDeadlock3();

    }

    // DEADLOCK DEMO 1: Unordered lock acquisition
    private static void demonstrateDeadlock1() {
        TestDeadlocks.DeadlockDemo1_UnorderedLocks demo = new TestDeadlocks.DeadlockDemo1_UnorderedLocks(10);

        Thread thread1 = Thread.ofVirtual().name("DeadlockThread-1").start(() -> {
            demo.transferWithDeadlock(5, 3, 100);
        });

        Thread thread2 = Thread.ofVirtual().name("DeadlockThread-2").start(() -> {
            demo.transferWithDeadlock(3, 5, 50);
        });

        try {
            Thread.sleep(5000);
            System.out.println("DEADLOCK OCCURRED! Threads are stuck waiting for each other.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // DEADLOCK DEMO 2: Nested synchronized blocks
    private static void demonstrateDeadlock2() {
        TestDeadlocks.DeadlockDemo2_NestedSynchronized demo = new TestDeadlocks.DeadlockDemo2_NestedSynchronized();

        Thread thread1 = Thread.ofVirtual().name("SyncThread-1").start(() -> {
            demo.transferAtoB(100);
        });

        Thread thread2 = Thread.ofVirtual().name("SyncThread-2").start(() -> {
            demo.transferBtoA(50);
        });

        try {
            Thread.sleep(5000);
            System.out.println("DEADLOCK OCCURRED! Nested synchronized blocks locked each other out.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // DEADLOCK DEMO 3: Circular waiting chain
    private static void demonstrateDeadlock3() {
        TestDeadlocks.DeadlockDemo3_WaitingChain demo = new TestDeadlocks.DeadlockDemo3_WaitingChain();

        Thread thread1 = Thread.ofVirtual().name("ChainThread-1").start(() -> {
            try {
                demo.operationABC();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread thread2 = Thread.ofVirtual().name("ChainThread-2").start(() -> {
            try {
                demo.operationBCA();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread thread3 = Thread.ofVirtual().name("ChainThread-3").start(() -> {
            try {
                demo.operationCAB();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            Thread.sleep(5000);
            System.out.println("DEADLOCK OCCURRED! Circular waiting chain created a deadlock.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
