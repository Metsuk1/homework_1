package com.example.homework_1.hw03;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomHashMap <K,V> implements Map<K,V> {
    private HashNode<K,V>[] chainArr; // array of hash nodes
    private int M = 11; // capacity
    private int size; // number of key-value pairs

    @Setter
    @Getter
    private class HashNode<K,V> {
        K key;
        V value;
        HashNode<K,V> next;

        public HashNode(K key, V value) {
            setKey(key);
            setValue(value);
        }
    }

    public CustomHashMap() {
        chainArr = new HashNode[M];
        size = 0;
    }

    public CustomHashMap(int M){
        this.M = M;
        chainArr = new HashNode[M];
        size = 0;
    }

    private int hash(K key) {
        return  Math.abs(key.hashCode()) % M;
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

        for(int i = 0; i < M; i++) {
            HashNode<K,V> current = chainArr[i];

            while(current != null) {
                if(current.getKey().equals(key)) {

                    return true;
                }
                current = current.next;
            }
        }

        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        checkValue((V) value);

        for(int i = 0; i < M; i++) {
            HashNode<K,V> current = chainArr[i];

            while(current != null) {
                if(current.getValue().equals(value)) {

                    return true;
                }
                current = current.next;
            }
        }

        return false;
    }

    @Override
    public V get(Object key) {
        checkKey((K) key);

        int index = hash((K) key);
        HashNode<K,V> current = chainArr[index];

        while(current != null) {
            if(current.getKey().equals(key)) {

                return current.getValue();
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
        HashNode<K,V> current = chainArr[index];

        while(current != null) {
            if(current.getKey().equals(key)) {
                V oldValue = current.getValue();
                current.setValue(value);

                return oldValue;
            }
            current = current.next;
        }
        HashNode<K,V> newNode = new HashNode<>(key, value);
        newNode.next = chainArr[index];
        chainArr[index] = newNode;
        size++;

        return null;
    }

    @Override
    public V remove(Object key) {
        checkKey((K) key);
        int index = hash((K) key);
        HashNode<K,V> current = chainArr[index];
        HashNode<K,V> prev = null;

        while(current != null) {
            if(current.getKey().equals(key)) {
                V oldValue = current.getValue();

                if(prev == null) {
                    chainArr[index] = current.next;
                }else{
                    prev.next = current.next;
                }
                size--;

                return oldValue;
            }else{
                prev = current;
                current = current.next;
            }
        }

        return null;// if key does not found
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {
        chainArr = new HashNode[M];
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        return Set.of();
    }

    @Override
    public Collection<V> values() {
        return List.of();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return Set.of();
    }

    private void checkValue(V value) {
        if(value == null){
            throw new NullPointerException("value is not be null");
        }
    }

    private void checkKey(K key) {
        if(key == null){
            throw new NullPointerException("key is not be null");
        }
    }
}
