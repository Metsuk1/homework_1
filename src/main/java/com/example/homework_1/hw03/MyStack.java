package com.example.homework_1.hw03;

import lombok.Setter;

@Setter
public class MyStack<T> {
    private MyLinkedList<T> list;

    public MyStack(MyLinkedList<T> list) {
        setList(list);
    }

    public T push(T item) {
        list.addFirst(item);
        return item;
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.size() == 0;
    }

    public T peek() {
        return list.getFirst();
    }

    public T pop() {
        T value = list.getFirst();
        list.remove(value);
        return value;
    }

}
