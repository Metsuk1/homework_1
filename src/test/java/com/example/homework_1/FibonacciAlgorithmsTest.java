package com.example.homework_1;


import com.example.homework_1.fibonacci.FibonacciAlgorithms;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(55, res);
    }

    @Test
    void testFibonacciIterative() {
        long res = fib.fibonacciIterative(10);
        assertEquals(55, res);
    }

    @Test
    void testFibonacciMemoized() {
        long res = fib.fibonacciMemoized(10);
        assertEquals(55, res);
    }

    @Test
    void compareFibonacciIterative_and_memoized() {
        assertEquals(fib.fibonacciIterative(10), fib.fibonacciMemoized(10));
    }

    @Test
    void checkCorrectnessFrom_0_to_35(){
        for(int i = 0; i <= 35; i++){
            long memorized = fib.fibonacciMemoized(i);
            long iterative = fib.fibonacciIterative(i);
            long recursive = fib.fibonacciRecursive(i);

            assertEquals(iterative, recursive);
            assertEquals(recursive, memorized);
        }
    }
}
