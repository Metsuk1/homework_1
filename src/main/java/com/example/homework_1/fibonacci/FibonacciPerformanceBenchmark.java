package com.example.homework_1.fibonacci;

import lombok.SneakyThrows;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FibonacciPerformanceBenchmark {
    @SneakyThrows
    public static void main(String[] args) {
        StringBuilder rep = new StringBuilder();
        rep.append("Performance Benchmark:\n");


        rep.append(runTest("Recursive")).append("\n");
        rep.append(runTest("Iterative")).append("\n");
        rep.append(runTest("Memorized")).append("\n");

        try(PrintWriter writer = new PrintWriter(new FileWriter("Performance benchmark.txt"))) {
            writer.write(rep.toString());
        }catch (IOException e) {
            System.out.println("Error to write to file " + e.getMessage());
        }

    }

    private static String runTest(String methodName) throws Exception {
        FibonacciAlgorithms fib = new FibonacciAlgorithms();
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        int[] nums = {10, 20, 30, 35};
        StringBuilder res = new StringBuilder();
        res.append("test").append(methodName).append("\n");

        for(int n : nums){
            res.append("Input:").append(n).append("\n");

            System.gc();
            Thread.sleep(100);

            long startUsedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024);
            long startTime = System.nanoTime();

            long result = switch (methodName.toLowerCase()){
                case "recursive" -> fib.fibonacciRecursive(n);
                case "iterative" -> fib.fibonacciIterative(n);
                case "memorized" -> fib.fibonacciMemoized(n);
                default -> throw new IllegalStateException("Unexpected value: " + methodName.toLowerCase());
            };

            long endTime = System.nanoTime();
            long endUsedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024);

            long elapsedTime = (endTime - startTime) / 1_000_000;
            long memoryUsedKb = endUsedMemory - startUsedMemory;

            res.append("n = ").append(n)
                    .append(" | Result: ").append(result)
                    .append(" | Time: ").append(elapsedTime).append(" ms")
                    .append(" | Memory: ").append(memoryUsedKb).append(" KB\n");
        }

        return res.toString();
    }





}
