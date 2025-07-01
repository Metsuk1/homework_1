package com.example.homework_1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CustomListTest {

    private CustomList<?> list;
    static Stream<List<?>> list_implementation(){
        return Stream.of(
                new ArrayList<>(),
                new CustomList<>()
        );
    }

    @BeforeEach
    void setUp() {
        list = new CustomList<>();
    }

    @AfterEach
    void tearDown() {
        list = null;
    }

    @Test
    void test_clear_list() {
        list.add(10);
        list.add(439);
        list.clear();
        assertTrue(list.isEmpty());
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

    @ParameterizedTest
    @MethodSource("list_implementation")
    void test_add_and_size_parameterized(List<Object> testList) {
        assertTrue(testList.isEmpty());
        testList.add("A");
        testList.add("B");
        assertEquals(2, testList.size());
    }

    @Test
    void test_removeElement() {
        assertTrue(list.isEmpty());
        list.add("smth");
        list.add("hello");
        assertTrue(list.remove("smth"));
        assertFalse(list.contains("smth"));
        assertEquals(1, list.size());
    }

    @ParameterizedTest
    @MethodSource("list_implementation")
    void test_remove_parameterized(List<Object> testList) {
        testList.add("A");
        testList.add("B");
        assertTrue(testList.remove("A"));
        assertFalse(testList.contains("A"));
        assertEquals(1, testList.size());
    }

    @Test
    void test_size() {
        assertTrue(list.isEmpty());
        list.add(1);
        list.add(2);
        assertEquals(2, list.size());
    }

    @Test
    void test_getElement() {
        assertTrue(list.isEmpty());
        list.add("Hello");
        list.add("World");
        assertEquals("Hello", list.get(0));
    }

    @Test
    void test_indexOf() {
        assertTrue(list.isEmpty());
        list.add(1);
        assertEquals(-1, list.indexOf("Hello"));
        assertEquals(0, list.indexOf(1));
    }

    @Test
    void removeByIndex() {
        list.add("Hello");
        list.add("World");
        list.add("!");

        String removed = (String) list.remove(0);
        assertEquals("Hello", removed);
        assertEquals(2, list.size());
        assertEquals("World", list.get(0));
    }

    @Test
    void test_Contains_and_IndexOf() {
        list.add("basketball");
        list.add("football");
        list.add("volleyball");
        assertTrue(list.contains("football"));
        assertEquals(1, list.indexOf("football"));
        assertEquals(-1, list.indexOf("John"));
    }

    @Test
    void test_toArray() {
        list.add(1);
        list.add(2);
        Object[] array = list.toArray();
        assertEquals(2, array.length);
        assertArrayEquals(new Object[]{1, 2}, array);
    }

    @Test
    void testIterator() {
        list.addAll(List.of("1", "2", "3"));
        Iterator<?> it = list.iterator();
        List<String> result = new ArrayList<>();
        while (it.hasNext()) {
            result.add((String) it.next());
        }
        assertEquals(List.of("1", "2", "3"), result);
    }

    @Test
    void test_setElement() {
        assertTrue(list.isEmpty());
        list.add("A");
        list.add("B");
        list.add("C");
        assertEquals("A", list.set(0, "D"));
        assertEquals("D", list.get(0));
    }

    @Test
    void test_contains() {
        list.add("one");
        list.add("two");
        list.add("three");
        assertTrue(list.contains("one"));
        assertFalse(list.contains("four"));
    }

    @Test
    void test_addAll() {
        List<String> fromAdd = List.of("one", "two", "three");
        assertTrue(list.addAll(fromAdd));
        assertEquals(3, list.size());
        assertEquals("one", list.get(0));
    }

    @ParameterizedTest
    @MethodSource("list_implementation")
    void testAddAllAtIndex(List<String> list) {
        list.add("one");
        list.add("two");
        List<String> to_add = List.of("zero", "three");
        assertTrue(list.addAll(1, to_add));
        assertEquals(List.of("one", "zero", "three", "two"), list);
    }

    @Test
    void compare_ArrayList_and_CustomList() {
        CustomList<String> customList = new CustomList<>();
        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("one");
        arrayList.add("two");
        arrayList.add("three");

        customList.addAll(arrayList);

        assertEquals(arrayList.size(), customList.size());

        for (int i = 0; i < customList.size(); i++) {
            assertEquals(arrayList.get(i), customList.get(i));
        }

        arrayList.remove(0);
        customList.remove(0);

        assertEquals(arrayList.get(0), customList.get(0));

        customList.clear();
        arrayList.clear();

        assertEquals(arrayList.size(), customList.size());
        assertTrue(customList.isEmpty());
        assertTrue(arrayList.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("list_implementation")
    void test_containsAll_parameterized(List<Object> testList) {
        testList.add("A");
        testList.add("B");
        testList.add("C");
        assertTrue(testList.containsAll(List.of("A", "C")));
        assertFalse(testList.containsAll(List.of("A", "D")));
    }


    @Test
    void test_increaseCapacity() {
        CustomList<Integer> list = new CustomList<>();

        int itemsCount = 1000;
        for (int i = 0; i < itemsCount; i++) {
            list.add(i);
        }
        assertEquals(itemsCount, list.size());
        for (int i = 0; i < itemsCount; i++) {
            assertEquals(i, list.get(i));
        }
    }

    @ParameterizedTest
    @MethodSource("list_implementation")
    void test_lastIndexOf_parameterized(List<Object> testList) {
        testList.add("A");
        testList.add("B");
        testList.add("A");
        assertEquals(2, testList.lastIndexOf("A"));
    }

    @ParameterizedTest
    @MethodSource("list_implementation")
    void test_set_parameterized(List<Object> testList) {
        testList.add("A");
        testList.add("B");
        assertEquals("A", testList.set(0, "X"));
        assertEquals("X", testList.get(0));
        assertEquals("B", testList.get(1));
    }
}
