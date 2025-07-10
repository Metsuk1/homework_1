package com.example.homework_1;


import com.example.homework_1.hw03.MyLinkedList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


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
    void test_add() {
        list.add("a");
        list.add("b");
        list.add("c");
        Assertions.assertEquals("a",list.getFirst());
    }

    @Test
    void test_remove() {
        list.add("a");
        list.add("b");
        list.add("c");

        list.remove("a");

        Assertions.assertEquals("b",list.getFirst());
    }

    @Test
    void test_contains() {
        list.add("a");
        list.add("b");
        list.add("c");

        assertTrue(list.contains("b"));
    }

    @Test
    void test_addFirst() {
        list.add("a");
        list.add("b");
        list.add("c");
        list.addFirst("d");

        Assertions.assertEquals("d",list.getFirst());
    }

    @Test
    void test_removeByIndex(){
        list.add("a");
        list.add("b");
        list.add("c");

        Object res = list.remove(0);

        Assertions.assertEquals("a",res);
        Assertions.assertEquals("b",list.getFirst());
    }

    @Test
    void test_getLast(){
        list.add("a");
        list.add("b");
        list.add("c");
        Assertions.assertEquals("c",list.getLast());
    }

    @Test
    void test_getByIndex(){
        list.add("a");
        list.add("b");
        list.add("c");
        Assertions.assertEquals("b",list.get(1));
    }
}
