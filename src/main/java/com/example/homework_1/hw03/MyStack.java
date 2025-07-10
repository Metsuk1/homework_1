package com.example.homework_1.hw03;


public class MyStack<T> {
    private CustomList<T> list;

    public MyStack(CustomList<T> list) {
        this.list = list;
    }

    public T push(T item) {
        list.add(item);
        return item;
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.size() == 0;
    }

    public T peek() {
        return list.get(list.size()-1);
    }

    public T pop() {
        T tmp = list.get(list.size() - 1);
        list.remove(list.size() - 1);
        return tmp;
    }

}
