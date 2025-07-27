package com.example.homework_1;

import com.example.homework_1.hw03.CustomHashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;

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

    @Test
    void test_keySet() {
        // Тест метода keySet()
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");

        Set<Integer> keys = map.keySet();

        Assertions.assertEquals(3, keys.size());
        Assertions.assertTrue(keys.contains(1));
        Assertions.assertTrue(keys.contains(2));
        Assertions.assertTrue(keys.contains(3));
        Assertions.assertFalse(keys.contains(4));
    }

    @Test
    void test_values() {
        // Тест метода values()
        map.put(1, "hello");
        map.put(2, "world");
        map.put(3, "test");

        Collection<String> values = map.values();

        Assertions.assertEquals(3, values.size());
        Assertions.assertTrue(values.contains("hello"));
        Assertions.assertTrue(values.contains("world"));
        Assertions.assertTrue(values.contains("test"));
        Assertions.assertFalse(values.contains("notfound"));
    }

    @Test
    void test_emptyMapOperations() {
        // Тесты на пустой мапе
        Assertions.assertTrue(map.isEmpty());
        Assertions.assertEquals(0, map.size());
        Assertions.assertNull(map.get(1));
        Assertions.assertFalse(map.containsKey(1));
        Assertions.assertFalse(map.containsValue("test"));
        Assertions.assertNull(map.remove(1));

        // Проверяем пустые коллекции
        Assertions.assertTrue(map.keySet().isEmpty());
        Assertions.assertTrue(map.values().isEmpty());
        Assertions.assertTrue(map.entrySet().isEmpty());
    }

    @Test
    void test_hashCollisions() {
        map.put(1, "first");
        map.put(12, "collision");
        map.put(23, "another_collision");

        Assertions.assertEquals(3, map.size());
        Assertions.assertEquals("first", map.get(1));
        Assertions.assertEquals("collision", map.get(12));
        Assertions.assertEquals("another_collision", map.get(23));

        String removed = map.remove(12);
        Assertions.assertEquals("collision", removed);
        Assertions.assertEquals(2, map.size());
        Assertions.assertNull(map.get(12));
        Assertions.assertEquals("first", map.get(1));
        Assertions.assertEquals("another_collision", map.get(23));
    }


}
