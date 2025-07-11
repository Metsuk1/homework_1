package com.example.homework_1;

import com.example.homework_1.hw03.MyLinkedList;
import com.example.homework_1.hw03.MyStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyStackTest {
    MyStack<String> stack;

    @BeforeEach
    void setUp() {
        stack = new MyStack<>(new MyLinkedList<>());
    }

    @AfterEach
    void tearDown() {
        stack = null;
    }

    @Test
    void test_push_and_pop() {
        stack.push("a");
        stack.push("b");
        stack.push("c");

        assertEquals("c", stack.pop());
        assertEquals("b", stack.pop());
        assertEquals("a", stack.pop());
    }

    @Test
    void test_isEmpty() {
        stack.push("hello");
        stack.push("world");

        Assertions.assertFalse(stack.isEmpty());
    }

    @Test
    void testPeek() {

        stack.push("x");
        assertEquals("x", stack.peek());

        stack.push("y");
        assertEquals("y", stack.peek());

        stack.pop();
        assertEquals("x", stack.peek());
    }
}
