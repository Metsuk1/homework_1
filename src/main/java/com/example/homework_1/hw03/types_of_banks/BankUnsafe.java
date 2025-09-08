package com.example.homework_1.hw03.types_of_banks;

import java.math.BigInteger;
import java.util.Random;

public class BankUnsafe {
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