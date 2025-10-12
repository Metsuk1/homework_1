package com.example.homework_1.hw03.types_of_banks;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class BankAtomic{
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