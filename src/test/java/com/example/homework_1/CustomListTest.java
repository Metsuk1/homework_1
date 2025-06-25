package com.example.homework_1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomListTest {
    private CustomList<?> list;

    @BeforeEach
    void setUp() {
        list = new CustomList<>();
    }

    @Test
    void test_clear_list() {
        list.add(10);
        list.add(439);
        list.clear();
        Assertions.assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void test_addElement() {
        list.add(12);
        list.add("Hello");
        assertEquals(12, list.get(0));
        assertEquals("Hello", list.get(1));
        assertEquals(2, list.size());
    }

    @Test
    void test_removeElement() {
        Assertions.assertTrue(list.isEmpty());
        list.add("smth");
        list.add("hello");
        list.remove("smth");
        Assertions.assertFalse(list.contains("smth"));
    }

    @Test
    void test_size(){
        Assertions.assertTrue(list.isEmpty());
        list.add(1);
        list.add(2);
        assertEquals(2, list.size());
    }

    @Test
    void test_getElement() {
        Assertions.assertTrue(list.isEmpty());
        list.add("Hello");
        list.add("World");
        assertEquals("Hello", list.get(0));
    }

    @Test
    void test_indexOf(){
        Assertions.assertTrue(list.isEmpty());
        list.add(1);
        assertEquals(-1, list.indexOf("Hello"));
    }

    @Test
    void removeByIndex(){
        list.add("Hello");
        list.add("World");
        list.add("!");

        String removed = (String) list.remove(0);
        assertEquals("Hello", removed);
        assertEquals(2, list.size());
    }
    @Test
    void test_Contains_and_IndexOf() {
        list.add("basketball");
        list.add("football");
        list.add("volleyball");
        Assertions.assertTrue(list.contains("football"));
        assertEquals(1, list.indexOf("football"));
        assertEquals(-1, list.indexOf("John"));
    }

    @Test
    void test_toArray() {
        list.add(1);
        list.add(2);
        Object[] array = list.toArray();
        assertEquals(2, array.length);
        Assertions.assertArrayEquals(new Object[]{1, 2}, array);
    }

    @Test
    void testIterator() {
        list.addAll(List.of("1", "2", "3"));
        Iterator<?> it = list.iterator();
        List<String> result = new CustomList<>();
        while (it.hasNext()) {
            result.add((String) it.next());
        }
        assertEquals(List.of("1", "2", "3"), result);
    }


    @Test
    void test_setElement(){
        Assertions.assertTrue(list.isEmpty());
        list.add("A");
        list.add("B");
        list.add("C");
        list.set(0, "D");
        assertEquals("D", list.get(0));
    }

    @Test
    void test_contains() {
        list.add("one");
        list.add("two");
        list.add("three");
        Assertions.assertTrue(list.contains("one"));
        Assertions.assertFalse(list.contains("four"));
    }

    @Test
    void test_addAll() {
        List<?> fromAdd = List.of("one", "two", "three");
        Assertions.assertTrue(list.addAll(fromAdd));
        assertEquals(fromAdd, list);
    }

    @Test
    void compare_ArrayList_and_CustomList(){
        CustomList<String> customList = new CustomList<>();
        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("one");
        arrayList.add("two");
        arrayList.add("three");

        customList.addAll(arrayList);

        assertEquals(arrayList.size(), customList.size());

        for(int i = 0; i < customList.size(); i++){
            assertEquals(arrayList.get(i), customList.get(i));
        }

        arrayList.remove(0);
        customList.remove(0);

        assertEquals(arrayList.get(0), customList.get(0));

        customList.clear();
        arrayList.clear();

        assertEquals(arrayList.size(), customList.size());
        Assertions.assertTrue(customList.isEmpty());
        Assertions.assertTrue(arrayList.isEmpty());
    }

    @Test
    void test_increaseCapacity(){
        CustomList<Integer> list = new CustomList<>();

        int item = 1000;
        for (int i = 0; i < item; i++) {
            list.add(i);
        }
        assertEquals(item, list.size());
        for(int i = 0; i < item; i++){
            assertEquals(i, list.get(i));
        }
    }


    @AfterEach
    void tearDown() {
        list.clear();
    }

}
