package com.example.homework_1.tests;


import com.example.homework_1.fibonacci.FibonacciAlgorithms;
import com.example.homework_1.runner.Assert;
import com.example.homework_1.runner.annotations.BeforeEach;
import com.example.homework_1.runner.annotations.Test;
import com.example.homework_1.runner.annotations.AfterEach;


public class FibonacciAlgorithmsTest {

    private FibonacciAlgorithms fib;


    @BeforeEach
    void setUp() {
        fib = new FibonacciAlgorithms();
    }

    @AfterEach
    void tearDown() {
        fib = null;
    }

    @Test
     void testFibonacciRecursive() {
        long res = fib.fibonacciRecursive(10);
        Assert.assertEquals(55, res);
    }

    @Test
    void testFibonacciIterative() {
        long res = fib.fibonacciIterative(10);
        Assert.assertEquals(55, res);
    }

    @Test
    void testFibonacciMemoized() {
        long res = fib.fibonacciMemoized(10);
        Assert.assertEquals(55, res);
    }

    @Test
    void compareFibonacciIterative_and_memoized() {
        Assert.assertEquals(fib.fibonacciIterative(10), fib.fibonacciMemoized(10));
    }

    @Test
    void checkCorrectnessFrom_0_to_35(){
        for(int i = 0; i <= 35; i++){
            long memorized = fib.fibonacciMemoized(i);
            long iterative = fib.fibonacciIterative(i);
            long recursive = fib.fibonacciRecursive(i);

            Assert.assertEquals(iterative, recursive);
            Assert.assertEquals(recursive, memorized);
        }
    }
}
