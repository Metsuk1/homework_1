package com.example.homework_1;


import com.example.homework_1.hw03.MyLinkedList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class LinkedListTest {
    MyLinkedList<String> list;


    @BeforeEach
    void setUp() {
        list  = new MyLinkedList<>();
    }

    @AfterEach
    void tearDown() {
        list = null;
    }

    @Test
    void testAdd() {
        list.add("a");
        list.add("b");
        list.add("c");
        Assertions.assertEquals("a",list.getFirst());
    }

    @Test
    void testRemove() {
        list.add("a");
        list.add("b");
        list.add("c");

        list.remove("a");
        Assertions.assertEquals("b",list.getFirst());
    }
}
