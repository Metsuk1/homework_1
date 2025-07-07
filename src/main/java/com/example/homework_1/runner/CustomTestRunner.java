package com.example.homework_1.runner;

import com.example.homework_1.runner.annotations.AfterEach;
import com.example.homework_1.runner.annotations.BeforeEach;
import com.example.homework_1.runner.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

public class CustomTestRunner {
    private final String packageName;
    private int classesScanned = 0;
    private int testsDiscovered = 0;
    private int testsPassed = 0;
    private int testsFailed = 0;
    private long totalExecutionTime = 0;
    private final List<String> results = new ArrayList<>();

    public CustomTestRunner(String packageName) {
        this.packageName = packageName;
    }

    public static void main(String[] args) {
        String packageName = "com.example.homework_1.tests";
        CustomTestRunner runner = new CustomTestRunner(packageName);
        runner.run();
    }

    public void run() {
        try {
            Set<Class<?>> classes = ClassScanner.getClasses(packageName);
            classesScanned = classes.size();

            for (Class<?> testClass : classes) {
                processTestClass(testClass);
            }

            printResults();
        } catch (Exception e) {
            System.err.println("Error running tests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processTestClass(Class<?> testClass) {
        Object testInstance = createTestInstance(testClass);
        if (testInstance == null) return;

        List<Method> beforeEachMethods = getAnnotatedMethods(testClass, BeforeEach.class);
        List<Method> afterEachMethods = getAnnotatedMethods(testClass, AfterEach.class);

        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                processTestMethod(testInstance, method, beforeEachMethods, afterEachMethods);
            }
        }
    }

    private Object createTestInstance(Class<?> testClass) {
        try {
            return testClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            results.add("✗ " + testClass.getSimpleName() + " - Failed to instantiate: " + e.getMessage());
            testsFailed++;
            return null;
        }
    }

    private void processTestMethod(Object testInstance, Method testMethod,
                                   List<Method> beforeEachMethods,
                                   List<Method> afterEachMethods) {
        Test testAnnotation = testMethod.getAnnotation(Test.class);
        String testName = testMethod.getDeclaringClass().getSimpleName() + "." + testMethod.getName();
        testsDiscovered++;

        if (!testAnnotation.description().isEmpty()) {
            testName += " (" + testAnnotation.description() + ")";
        }

        long startTime = System.currentTimeMillis();
        try {
            runLifecycleMethods(testInstance, beforeEachMethods);

            testMethod.setAccessible(true);
            if (testAnnotation.timeout() > 0) {
                runTestWithTimeout(testInstance, testMethod, testAnnotation.timeout());
            } else {
                testMethod.invoke(testInstance);
            }

            // Check for expected exception
            if (testAnnotation.expected() != Test.None.class) {
                results.add("✗ " + testName + " - Expected exception " + testAnnotation.expected().getSimpleName());
                testsFailed++;
            } else {
                long duration = System.currentTimeMillis() - startTime;
                results.add("✓ " + testName + " (" + duration + "ms)");
                testsPassed++;
                totalExecutionTime += duration;
            }
        } catch (InvocationTargetException e) {
            handleTestException(e, testAnnotation, testName, startTime);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            results.add("✗ " + testName + " (" + duration + "ms) - " + e.getClass().getSimpleName());
            testsFailed++;
            totalExecutionTime += duration;
        } finally {
            try {
                runLifecycleMethods(testInstance, afterEachMethods);
            } catch (Exception e) {
                System.err.println("Error in @AfterEach method: " + e.getMessage());
            }
        }
    }

    private void handleTestException(InvocationTargetException e, Test testAnnotation,
                                     String testName, long startTime) {
        Throwable targetException = e.getTargetException();
        long duration = System.currentTimeMillis() - startTime;

        if (testAnnotation.expected().isInstance(targetException)) {
            results.add("✓ " + testName + " (" + duration + "ms)");
            testsPassed++;
            totalExecutionTime += duration;
        } else {
            String failureMessage = targetException.getClass().getSimpleName();
            if (targetException.getMessage() != null) {
                failureMessage += ": " + targetException.getMessage();
            }
            results.add("✗ " + testName + " (" + duration + "ms) - " + failureMessage);
            testsFailed++;
            totalExecutionTime += duration;
        }
    }

    private void runTestWithTimeout(Object testInstance, Method testMethod, long timeout)
            throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            try {
                testMethod.invoke(testInstance);
                return null;
            } catch (InvocationTargetException e) {
                return e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        try {
            Object result = future.get(timeout, TimeUnit.MILLISECONDS);
            if (result instanceof InvocationTargetException) {
                throw (InvocationTargetException) result;
            }
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new TimeoutException("Test timed out after " + timeout + "ms");
        } finally {
            executor.shutdownNow();
        }
    }

    private void runLifecycleMethods(Object testInstance, List<Method> methods) throws Exception {
        for (Method method : methods) {

            method.invoke(testInstance);
        }
    }

    private List<Method> getAnnotatedMethods(Class<?> testClass, Class<? extends Annotation> annotation) {
        List<Method> methods = new ArrayList<>();
        Class<?> currentClass = testClass;

        while (currentClass != Object.class) {
            for (Method method : currentClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    method.setAccessible(true);
                    methods.add(method);
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        return methods;
    }

    private void printResults() {
        System.out.println("=== Custom Test Runner Results ===");
        System.out.println("Package: " + packageName);
        System.out.println("Classes scanned: " + classesScanned);
        System.out.println("Tests discovered: " + testsDiscovered);
        System.out.println("\nTest Results:");

        results.forEach(System.out::println);

        System.out.println("\nSummary:");
        System.out.println("Total tests: " + testsDiscovered);
        System.out.println("Passed: " + testsPassed);
        System.out.println("Failed: " + testsFailed);
        System.out.println("Total execution time: " + totalExecutionTime + "ms");

        double successRate = testsDiscovered == 0 ? 0 : (double) testsPassed / testsDiscovered * 100;
        System.out.printf("Success rate: %.1f%%\n", successRate);
    }
}