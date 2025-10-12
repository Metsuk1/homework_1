package com.example.homework_1.hw03.types_of_banks;

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