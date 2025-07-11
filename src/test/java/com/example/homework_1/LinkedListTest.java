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
    void test_addLast_and_getLast() {
        list.addLast("x");
        list.addLast("y");
        list.addLast("z");

        Assertions.assertEquals("z", list.getLast());
        Assertions.assertEquals(3, list.size());
    }

    @Test
    void test_add_on_emptyList() {
        list.add("first");

        Assertions.assertEquals("first", list.getFirst());
        Assertions.assertEquals("first", list.getLast());
        Assertions.assertEquals(1, list.size());
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

    @Test
    void test_removeFirst_onSingleElement() {
        list.add("only");
        String removed = list.removeFirst();

        Assertions.assertEquals("only", removed);
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    void test_removeLast_onSingleElement() {
        list.add("single");
        String removed = list.removeLast();

        Assertions.assertEquals("single", removed);
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    void test_removeLast_multipleElements() {
        list.add("1");
        list.add("2");
        list.add("3");

        String removed = list.removeLast();

        Assertions.assertEquals("3", removed);
        Assertions.assertEquals("2", list.getLast());
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void test_clear() {
        list.add("a");
        list.add("b");

        list.clear();

        Assertions.assertEquals(0, list.size());
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    void test_indexOf_and_lastIndexOf() {
        list.add("a");
        list.add("b");
        list.add("a");

        Assertions.assertEquals(0, list.indexOf("a"));
        Assertions.assertEquals(2, list.lastIndexOf("a"));
    }
    @Test
    void test_set_by_index() {
        list.add("old");
        list.set(0, "new");

        Assertions.assertEquals("new", list.get(0));
    }

    @Test
    void test_iterator_traversal() {
        list.add("a");
        list.add("b");
        list.add("c");

        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
        }
        Assertions.assertEquals("abc", sb.toString());
    }
}
