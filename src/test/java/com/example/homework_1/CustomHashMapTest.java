package com.example.homework_1;

import com.example.homework_1.hw03.CustomHashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CustomHashMapTest {
    CustomHashMap<Integer,String> map;

    @BeforeEach
    void setUp() {
        map = new CustomHashMap<>();
    }

    @AfterEach
    void tearDown() {
        map = null;
    }

    @Test
    void test_put() {
        map.put(1,"Hello");
        map.put(2,"World");
        map.put(3,"!");
        map.put(1,"First");


        Assertions.assertEquals("First",map.get(1));
        Assertions.assertEquals(3,map.size());
    }

    @Test
    void test_containsKey() {
        map.put(1,"Hello");
        map.put(2,"World");
        map.put(3,"!");

        Assertions.assertTrue(map.containsKey(1));
        Assertions.assertTrue(map.containsValue("World"));
    }

    @Test
    void test_remove() {
        map.put(1,"Hello");
        map.put(2,"World");

        String removedValue = map.remove(2);

        Assertions.assertEquals("World", removedValue);
        Assertions.assertFalse(map.containsKey(2));
        Assertions.assertEquals(1, map.size());

        Assertions.assertNull(map.remove(999));
        Assertions.assertEquals(1, map.size());

        map.remove(1);
        Assertions.assertTrue(map.isEmpty());
    }

    @Test
    void test_get() {
        map.put(1,"Hello");
        map.put(2,"World");

        Assertions.assertEquals("Hello",map.get(1));
    }

    @Test
    void test_clear(){
        map.put(1,"Hello");
        map.put(2,"World");
        map.clear();

        Assertions.assertTrue(map.isEmpty());
        Assertions.assertNull(map.get(1));
    }


}
