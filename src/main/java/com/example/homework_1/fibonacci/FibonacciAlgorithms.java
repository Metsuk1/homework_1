package com.example.homework_1.fibonacci;

import java.util.HashMap;

public class FibonacciAlgorithms {
    private static HashMap<Integer,Long> cache = new HashMap<>();

/*
Time complexity is: O(2^n) because each call fibonacciRecursive(n)results in two recursive calls
Space complexity:O(n) because of the call stack, the maximum depth is n
 */
    public long fibonacciRecursive(int n) {
        if(n < 2) return n;
        return fibonacciRecursive(n-1) + fibonacciRecursive(n-2);
    }

    /*
    Time complexity is: O(n)
    Space complexity:O(n)
    uses bottom-up approach with only two variables
     to track previous values, eliminating recursion overhead.
    */
    public long fibonacciIterative(int n) {
        if(n < 2) return n;

        int current = 1;
        int previous = 0;
        int tmp;
        for(int i = 2; i <= n; i++) {
            tmp = current + previous;
            previous = current;
            current = tmp;
        }

        return current;
    }

    /*
    Time complexity is: O(n) it depends of n;it's the number which we want to compute the fibonacci
    Space complexity:O(n)
    we use extra memory for avoiding repeating calculations
     */
    public long fibonacciMemoized(int n) {
        if(n < 2) return n;
        if(cache.containsKey(n)) return cache.get(n);

        long res = fibonacciMemoized(n-1) + fibonacciMemoized(n-2);
        cache.put(n,res);

        return res;
    }
}
