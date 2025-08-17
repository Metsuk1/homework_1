package com.example.homework_1;

import com.example.homework_1.hw03.MyLinkedList;
import com.example.homework_1.hw03.MyQueue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyQueueTest {
    MyQueue<String> myQueue;

    @BeforeEach
    void setUp() {
        myQueue = new MyQueue<>(new MyLinkedList<>());
    }

    @AfterEach
    void tearDown() {
        myQueue = null;
    }

    @Test
    void test_offer() {
        Assertions.assertTrue(myQueue.offer("1"));
        Assertions.assertTrue(myQueue.offer("2"));
        assertEquals(2,myQueue.size());
    }

    @Test
    void test_poll() {
        myQueue.offer("a");
        myQueue.offer("b");

        assertEquals("a", myQueue.poll());
        assertEquals("b", myQueue.poll());
        Assertions.assertNull(myQueue.poll());
    }

    @Test
    void testPeek() {
        Assertions.assertNull(myQueue.peek());

        myQueue.offer("x");
        assertEquals("x", myQueue.peek());

        myQueue.offer("y");
        assertEquals("x", myQueue.peek());
    }


}
