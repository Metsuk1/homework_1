package com.example.homework_1.hw03.tests;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class TestDeadlocks {

    public static class DeadlockDemo1_UnorderedLocks {
        private final ReentrantLock[] locks;
        private final long[] accounts;
        private final Random random = new Random();

        public DeadlockDemo1_UnorderedLocks(int numberOfAccounts) {
            this.accounts = new long[numberOfAccounts];
            this.locks = new ReentrantLock[numberOfAccounts];
            for (int i = 0; i < numberOfAccounts; i++) {
                locks[i] = new ReentrantLock();
                accounts[i] = 1000;
            }
        }

        public void transferWithDeadlock(int from, int to, long amount) {
            System.out.println(Thread.currentThread().getName() +
                    " trying to transfer from " + from + " to " + to);

            locks[from].lock(); // Lock first account
            System.out.println(Thread.currentThread().getName() +
                    " acquired lock for account " + from);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}

            locks[to].lock(); // Lock second account
            try {
                System.out.println(Thread.currentThread().getName() +
                        " acquired lock for account " + to + " - performing transfer");
                accounts[from] -= amount;
                accounts[to] += amount;
            } finally {
                locks[to].unlock();
            }

            locks[from].unlock();
        }

        public int pickRandomAccount() {
            return random.nextInt(accounts.length);
        }
    }


    public static class DeadlockDemo2_NestedSynchronized {
        private final Object lock1 = new Object();
        private final Object lock2 = new Object();
        private long balance1 = 1000;
        private long balance2 = 1000;

        public void transferAtoB(long amount) {
            System.out.println(Thread.currentThread().getName() +
                    " starting transfer A->B");

            synchronized (lock1) { // Lock A first
                System.out.println(Thread.currentThread().getName() +
                        " acquired lock1");

                try {
                    Thread.sleep(10); // Increase deadlock probability
                } catch (InterruptedException e) {}

                synchronized (lock2) { // Then lock B - POTENTIAL DEADLOCK!
                    System.out.println(Thread.currentThread().getName() +
                            " acquired lock2 - performing transfer A->B");
                    balance1 -= amount;
                    balance2 += amount;
                }
            }
        }

        public void transferBtoA(long amount) {
            System.out.println(Thread.currentThread().getName() +
                    " starting transfer B->A");

            synchronized (lock2) { // Lock B first
                System.out.println(Thread.currentThread().getName() +
                        " acquired lock2");

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}

                synchronized (lock1) { // Then lock A
                    System.out.println(Thread.currentThread().getName() +
                            " acquired lock1 - performing transfer B->A");
                    balance2 -= amount;
                    balance1 += amount;
                }
            }
        }
    }

    public static class DeadlockDemo3_WaitingChain {
        private final ReentrantLock lockA = new ReentrantLock();
        private final ReentrantLock lockB = new ReentrantLock();
        private final ReentrantLock lockC = new ReentrantLock();

        // DEADLOCK CAUSE 3: Circular waiting chain
        // Thread 1: A -> B -> C
        // Thread 2: B -> C -> A
        // Thread 3: C -> A -> B
        public void operationABC() throws InterruptedException {
            System.out.println(Thread.currentThread().getName() +
                    " starting operation A->B->C");

            lockA.lock();
            System.out.println(Thread.currentThread().getName() + " acquired lockA");
            try {
                Thread.sleep(50);
                lockB.lock();
                System.out.println(Thread.currentThread().getName() + " acquired lockB");
                try {
                    Thread.sleep(50);
                    lockC.lock(); // pottential deadlock
                    try {
                        System.out.println(Thread.currentThread().getName() +
                                " acquired lockC - operation complete");
                    } finally {
                        lockC.unlock();
                    }
                } finally {
                    lockB.unlock();
                }
            } finally {
                lockA.unlock();
            }
        }

        public void operationBCA() throws InterruptedException {
            System.out.println(Thread.currentThread().getName() +
                    " starting operation B->C->A");

            lockB.lock();
            System.out.println(Thread.currentThread().getName() + " acquired lockB");
            try {
                Thread.sleep(50);
                lockC.lock();
                System.out.println(Thread.currentThread().getName() + " acquired lockC");
                try {
                    Thread.sleep(50);
                    lockA.lock(); // potential deadlock
                    try {
                        System.out.println(Thread.currentThread().getName() +
                                " acquired lockA - operation complete");
                    } finally {
                        lockA.unlock();
                    }
                } finally {
                    lockC.unlock();
                }
            } finally {
                lockB.unlock();
            }
        }

        public void operationCAB() throws InterruptedException {
            System.out.println(Thread.currentThread().getName() +
                    " starting operation C->A->B");

            lockC.lock();
            System.out.println(Thread.currentThread().getName() + " acquired lockC");
            try {
                Thread.sleep(50);
                lockA.lock();
                System.out.println(Thread.currentThread().getName() + " acquired lockA");
                try {
                    Thread.sleep(50);
                    lockB.lock(); // potential deadlock
                    try {
                        System.out.println(Thread.currentThread().getName() +
                                " acquired lockB - operation complete");
                    } finally {
                        lockB.unlock();
                    }
                } finally {
                    lockA.unlock();
                }
            } finally {
                lockC.unlock();
            }
        }
    }
}
