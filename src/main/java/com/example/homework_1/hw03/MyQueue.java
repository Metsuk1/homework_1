package com.example.homework_1.hw03;

import lombok.Setter;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;

@Setter
public class MyQueue <T> extends AbstractQueue<T> {
    private MyLinkedList<T> list;

    public MyQueue(MyLinkedList<T> list) {
        setList(list);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < list.size();
            }

            @Override
            public T next() {
                if(!hasNext())throw new NoSuchElementException();
                return list.get(i++);
            }
        };
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean offer(T elem) {
        if(elem == null)throw new NullPointerException();
        list.addLast(elem);
        return true;
    }

    @Override
    public T poll() {
        T head = checkIfEmpty();
        if(head != null){
            list.removeFirst();

        }

        return head;
    }

    @Override
    public T peek() {
        return list.isEmpty() ? null : list.getFirst();
    }

    private T checkIfEmpty() {
        if (list.isEmpty()) {
            return null;
        } else {
            return list.getFirst();
        }
    }
}
