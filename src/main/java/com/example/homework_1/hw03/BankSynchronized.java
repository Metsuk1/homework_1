package com.example.homework_1.hw03;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

// Using synchronized methods
public class BankSynchronized {
    private long[] accounts;
    private final Random rand;

    public BankSynchronized(int numberOfAccounts, long minBalance, long maxBalance) {
        this.accounts = new long[numberOfAccounts];
        this.rand = new Random();

        for (int i = 0; i < numberOfAccounts; i++) {
            accounts[i] = minBalance + rand.nextLong(maxBalance - minBalance + 1);
        }
    }

    public int pickRandomAccount() {
        return rand.nextInt(accounts.length);
    }

    public synchronized long getAccountBalance(int accountId) {
        return accounts[accountId];
    }

    public synchronized void setAccountBalance(int accountId, long newBalance) {
        accounts[accountId] = newBalance;
    }

    public synchronized void transfer(int from, int to, long amount) {
        if(accounts[from] >= amount) {
            accounts[from] -= amount;
            accounts[to] += amount;
        }
    }

    public synchronized BigInteger getAllBalanceOfAccounts() {
        BigInteger sum = BigInteger.ZERO;
        for(long balance : accounts) {
            sum = sum.add(BigInteger.valueOf(balance));
        }

        return sum;
    }
}

class BankWithLocks{
    private final long[] accounts;
    private final ReentrantLock[] locks;
    private final ReentrantLock globalLock;
    private final Random random;

    public BankWithLocks(int numberOfAccounts, long minBalance, long maxBalance) {
        this.accounts = new long[numberOfAccounts];
        this.locks = new ReentrantLock[numberOfAccounts];
        this.globalLock = new ReentrantLock();
        this.random = new Random();

        // Init locks
        for (int i = 0; i < numberOfAccounts; i++) {
            locks[i] = new ReentrantLock();
        }

        // Init accounts with random balances
        for (int i = 0; i < numberOfAccounts; i++) {
            accounts[i] = minBalance + random.nextLong(maxBalance - minBalance + 1);
        }
    }

    public int pickRandomAccount() {
        return random.nextInt(accounts.length);
    }

    public long getAccountBalance(int accountId) {
        locks[accountId].lock();
        try{
            return accounts[accountId];
        }finally {
            locks[accountId].unlock();
        }
    }

    public void setAccountBalance(int accountId, long newBalance) {
        locks[accountId].lock();
        try {
            accounts[accountId] = newBalance;
        } finally {
            locks[accountId].unlock();
        }
    }

    public void transfer(int from, int to, long amount) {
        if(from == to) return;

        int firstLock = Math.min(from, to);
        int secondLock = Math.max(from, to);

        locks[firstLock].lock();
        try {
            locks[secondLock].lock();
            try {
                if (accounts[from] >= amount) {
                    accounts[from] -= amount;
                    accounts[to] += amount;
                }
            } finally {
                locks[secondLock].unlock();
            }
        }finally {
            locks[firstLock].unlock();
        }
    }

    public BigInteger getAllBalanceOfAccounts() {
        globalLock.lock();

        try {
            // Lock all accounts in order
            for(ReentrantLock lock : locks) {
                lock.lock();
            }
            try {
                BigInteger sum = BigInteger.ZERO;
                for(long balance : accounts) {
                    sum = sum.add(BigInteger.valueOf(balance));
                }

                return sum;
            }finally {
                //unlock in reverse order
                for(int i = locks.length - 1; i >= 0; i--) {
                    locks[i].unlock();
                }
            }
        }finally {
            globalLock.unlock();
        }
    }
}

class BankAtomic{
    private final AtomicLong[] accounts;
    private final Random random;

    public BankAtomic(int numberOfAccounts, long minBalance, long maxBalance) {
        this.accounts = new AtomicLong[numberOfAccounts];
        this.random = new Random();

        for (int i = 0; i < numberOfAccounts; i++) {
            accounts[i] = new AtomicLong(minBalance + random.nextLong(maxBalance - minBalance + 1));
        }
    }

    public int pickRandomAccount() {
        return random.nextInt(accounts.length);
    }

    public long getAccountBalance(int accountId) {
        return accounts[accountId].get();
    }

    public void setAccountBalance(int accountId, long newBalance) {
        accounts[accountId].set(newBalance);
    }

    public boolean transfer(int from, int to, long amount) {
        if(from == to) return true;

        while (true){
            long fromBalance = accounts[from].get();
            long toBalance = accounts[to].get();

            if(fromBalance < amount) return false;

            if (accounts[from].compareAndSet(fromBalance, fromBalance - amount)) {
                while (!accounts[to].compareAndSet(toBalance, toBalance + amount)) {
                    toBalance = accounts[to].get();
                }
                return true;
            }
        }
    }

    public BigInteger getAllBalanceOfAccounts() {
        BigInteger sum = BigInteger.ZERO;
        for(AtomicLong balance : accounts) {
            sum = sum.add(BigInteger.valueOf(balance.get()));
        }

        return sum;
    }
}

class BankUnsafe {
    private final long[] accounts;
    private final Random random;

    public BankUnsafe(int numberOfAccounts, long minBalance, long maxBalance) {
        this.accounts = new long[numberOfAccounts];
        this.random = new Random();

        for (int i = 0; i < numberOfAccounts; i++) {
            accounts[i] = minBalance + random.nextLong(maxBalance - minBalance + 1);
        }
    }

    public int pickRandomAccountId() {
        return random.nextInt(accounts.length);
    }

    public long getAccountBalance(int accountId) {
        return accounts[accountId];
    }

    public void setAccountBalance(int accountId, long newBalance) {
        accounts[accountId] = newBalance;
    }

    public BigInteger getSumOfAllAccounts() {
        BigInteger sum = BigInteger.ZERO;
        for (long balance : accounts) {
            sum = sum.add(BigInteger.valueOf(balance));
        }
        return sum;
    }
}


