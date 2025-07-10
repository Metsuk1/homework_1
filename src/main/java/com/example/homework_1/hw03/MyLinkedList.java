package com.example.homework_1.hw03;


import java.util.*;

public class MyLinkedList <T> implements List<T> {
    private MyNode<T> head;
    private MyNode<T> tail;
    private int size;

    public MyLinkedList() {
        head = null;
        tail = null;
        size = 0;
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
    public boolean contains(Object o) {
        MyNode<T> myNode = head;

        while (myNode != null) {
            if(myNode.getData().equals(o)) {

                return true;
            }
            myNode = myNode.getNext();
        }

        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            MyNode<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if(!hasNext()) {
                    throw new NoSuchElementException();
                }
                T result = current.getData();
                current = current.getNext();
                return result;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        MyNode<T> current = head;
        int i = 0;

        while(current != null) {
            arr[i++] = current.getData();
            current = current.getNext();
        }

        return arr;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return null;
    }

    public void addFirst(T element){
        MyNode<T> node = head;
        head = new MyNode<>(element);
        node.setNext(head);
        size++;
    }

    public void addLast(T element){
        add(element);
    }

    @Override
    public boolean add(T t) {
        MyNode<T> newNode = new MyNode<>(t);

        if(head == null && tail == null) {
            head = tail = newNode;
        }
        else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }
        size++;

        return true;
    }

    @Override
    public boolean remove(Object o) {
        MyNode<T> current = head;

        while (current != null) {
            if (Objects.equals(current.getData(), o)) {
                if (current == head) {
                    head = current.getNext();

                    if (head != null) {
                        head.setPrev(null);
                    } else {
                        tail = null;
                    }
                } else if (current == tail) {
                    tail = tail.getPrev();
                    if (tail != null) {
                        tail.setNext(null);
                    }
                } else {
                    current.getPrev().setNext(current.getNext());
                    current.getNext().setPrev(current.getPrev());
                }
                size--;

                return true;
            }
            current = current.getNext();
        }

        return false;
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        if(c.isEmpty()){
            return false;
        }
        for(Object o : c){
            if(!contains(o)){
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if(c.isEmpty()) {
            return false;
        }
        for(T t : c) {
            addLast(t);
        }

        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if(c.isEmpty()) {
            return false;
        }
        int i = index;
        for(T t : c) {
            add(i++,t);
        }

        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if(c.isEmpty()) {
            return false;
        }
        boolean changed = false;
        for(Object o : c) {
            while(remove(o)) {
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        MyNode<T> current = head;

        while(current != null){
            MyNode<T> next = current.getNext();
            if(!c.contains(current.getData())) {
                remove(current.getData());
                changed = true;
            }
            current = next;
        }

        return changed;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public T get(int index) {
        MyNode<T> current = head;

        for(int i = 0; i < index; i++) {
            current = current.getNext();
        }

        return current.getData();
    }

    @Override
    public T getFirst(){
        check_list_is_empty();
        return head.getData();
    }

    @Override
    public T getLast(){
        check_list_is_empty();
        return tail.getData();
    }

    @Override
    public T set(int index, T element) {
        checkIndex(index);
        MyNode<T> current = head;

        for(int i = 0; i < index; i++) {
            current = current.getNext();
        }
        T oldValue = current.getData();
        current.setData(element);

        return oldValue;
    }

    @Override
    public void add(int index, T element) {
        checkIndexForAdd(index);

        if(index == 0){
            addFirst(element);
        }
        else if(index == size) {
            add(element);
        }
        else {
            MyNode<T> newNode = new MyNode<>(element);
            MyNode<T> current = head;

            for(int i = 0; i < index - 1;i++){
                current = current.getNext();
            }
            newNode.setNext(current.getNext());
            current.setNext(newNode);

            size++;
        }
    }

    @Override
    public T remove(int index) {
        checkIndex(index);
        MyNode<T> current = head;

        if(index == 0){
            T oldValue = current.getData();
            head = current.getNext();

            if(head != null) {
                head.setPrev(null);
            }
            else{
                tail = null;
                size--;
                return oldValue;
            }
        }
        for(int i = 0; i < index; i++){
            current = current.getNext();
        }
        T oldValue = current.getData();

        MyNode<T> prev = current.getPrev();
        MyNode<T> next = current.getNext();

        if (prev != null) prev.setNext(next);
        if (next != null) next.setPrev(prev);

        if (current == tail) tail = prev;

        size--;
        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        if(o == null) {
            return -1;
        }
        int index = 0;
        MyNode<T> current = head;

        while(current != null) {
            if(Objects.equals(current.getData(), o)) {
                return index;
            }
            current = current.getNext();
            index++;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if(o == null) {
            return -1;
        }
        int index = size - 1;
        MyNode<T> current = tail;

        while(current != null) {
            if(Objects.equals(current.getData(), o)) {
                return index;
            }
            current = current.getPrev();
            index++;
        }

        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return null;
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return null;
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return List.of();
    }

    private void check_list_is_empty() {
        if(head == null) {
            throw new NoSuchElementException("list is empty");
        }
    }

    private void checkIndex(int index) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
    }

    private void checkIndexForAdd(int index) {
        if(index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
    }
}
