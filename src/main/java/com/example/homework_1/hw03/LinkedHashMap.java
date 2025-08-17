package com.example.homework_1.hw03;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class LinkedHashMap<K,V> implements Map<K,V> {
    private HashNode<K,V>[] table;
    private HashNode<K,V> head;
    private HashNode<K,V> tail;
    private int size;
    private int capacity = 16;
    private static final double loadFactor = 0.75;

    @Setter
    @Getter
    private static class HashNode<K,V>{
        K key;
        V value;
        HashNode<K,V> next;
        HashNode<K,V> before;
        HashNode<K,V> after;

        public HashNode(K key, V value) {
            setKey(key);
            setValue(value);
        }
    }

    public LinkedHashMap() {
        table = new HashNode[capacity];
        size = 0;
        head = new HashNode<>(null, null);
        tail = new HashNode<>(null, null);
        head.after = tail;
        tail.before = head;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        checkKey((K) key);
        int index = hash(key);

        HashNode<K,V> current = table[index];
        while (current != null) {
            if (current.key.equals(key)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        checkValue((V) value);

        HashNode<K,V> current = head.after;
        while (current != null) {
            if (current.value.equals(value)) {
                return true;
            }
            current = current.after;
        }

        return false;
    }

    @Override
    public V get(Object key) {
        checkKey((K) key);
        int index = hash(key);

        HashNode<K,V> current = table[index];
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }

        return null;
    }

    @Override
    public V put(K key, V value) {
        checkKey(key);
        checkValue(value);

        int index = hash(key);
        HashNode<K,V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                V oldValue = current.value;
                current.value = value;
                return oldValue;
            }
            current = current.next;
        }

        HashNode<K,V> newNode = new HashNode<>(key, value);
        newNode.next = table[index];
        table[index] = newNode;
        addToTail(newNode);

        size++;
        resizeIfNeeded();

        return null;
    }

    @Override
    public V remove(Object key) {
        checkKey((K) key);

        int index = hash(key);
        HashNode<K,V> current = table[index];
        HashNode<K,V> prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                V oldValue = current.value;

                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }

                removeFromList(current);

                size--;
                return oldValue;
            }
            prev = current;
            current = current.next;
        }

        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {
        table = new HashNode[capacity];
        size = 0;
        head.after = tail;
        tail.before = head;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new LinkedHashSet<>();

        HashNode<K,V> current = head.after;
        while (current != tail) {
            keys.add(current.key);
            current = current.after;
        }

        return keys;
    }

    @Override
    public Collection<V> values() {
        List<V> vals = new ArrayList<>();

        HashNode<K,V> current = head.after;
        while (current != tail) {
            vals.add(current.value);
            current = current.after;
        }

        return vals;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return Set.of();
    }

    private int hash(Object key) {
        if (key == null) return 0;
        return Math.abs(key.hashCode()) % capacity;
    }

    private void checkKey(K key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
    }

    private void checkValue(V value) {
        if (value == null) {
            throw new NullPointerException("Value cannot be null");
        }
    }

    private void addToTail(HashNode<K,V> node) {
        node.before = tail.before;
        node.after = tail;
        tail.before.after = node;
        tail.before = node;
    }

    private void removeFromList(HashNode<K,V> node) {
        node.before.after = node.after;
        node.after.before = node.before;
    }

    private void moveToTail(HashNode<K,V> node) {
        removeFromList(node);
        addToTail(node);
    }

    private void resizeIfNeeded() {
        if (size >= capacity * loadFactor) {
            resize();
        }
    }
    private void resize() {
        HashNode<K,V>[] oldTable = table;
        int oldCapacity = capacity;

        capacity *= 2;
        table = new HashNode[capacity];

        // Перехешируем все элементы
        for (int i = 0; i < oldCapacity; i++) {
            HashNode<K,V> current = oldTable[i];

            while (current != null) {
                HashNode<K,V> next = current.next;

                int newIndex = hash(current.key);
                current.next = table[newIndex];
                table[newIndex] = current;

                current = next;
            }
        }
    }
}
