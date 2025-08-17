package com.example.homework_1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

public class LinkedHashMapTest {
    LinkedHashMap<Integer, String> map;

    @BeforeEach
    void setUp() {
        map = new LinkedHashMap<>();
    }

    @AfterEach
    void tearDown() {
        map = null;
    }

    @Test
    void test_insertionOrder() {
        map.put(3, "three");
        map.put(1, "one");
        map.put(2, "two");

        List<Integer> expectedOrder = Arrays.asList(3, 1, 2);
        List<Integer> actualOrder = new ArrayList<>(map.keySet());

        Assertions.assertEquals(expectedOrder, actualOrder);
    }

    @Test
    void test_valuesOrder() {
        map.put(10, "ten");
        map.put(5, "five");
        map.put(15, "fifteen");

        List<String> expectedOrder = Arrays.asList("ten", "five", "fifteen");
        List<String> actualOrder = new ArrayList<>(map.values());

        Assertions.assertEquals(expectedOrder, actualOrder);
    }

    @Test
    void test_entrySetOrder() {
        map.put(100, "hundred");
        map.put(50, "fifty");
        map.put(25, "twenty-five");

        List<Integer> keys = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            keys.add(entry.getKey());
        }

        List<Integer> expectedOrder = Arrays.asList(100, 50, 25);
        Assertions.assertEquals(expectedOrder, keys);
    }

    @Test
    void test_updateValueKeepsOrder() {
        map.put(1, "first");
        map.put(2, "second");
        map.put(3, "third");

        String oldValue = map.put(2, "updated_second");
        Assertions.assertEquals("second", oldValue);

        List<Integer> expectedOrder = Arrays.asList(1, 2, 3);
        List<Integer> actualOrder = new ArrayList<>(map.keySet());
        Assertions.assertEquals(expectedOrder, actualOrder);
        Assertions.assertEquals("updated_second", map.get(2));
    }

    @Test
    void test_removeKeepsOrder() {
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(4, "four");

        map.remove(2);

        List<Integer> expectedOrder = Arrays.asList(1, 3, 4);
        List<Integer> actualOrder = new ArrayList<>(map.keySet());
        Assertions.assertEquals(expectedOrder, actualOrder);
    }
}
