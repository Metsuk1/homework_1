package com.example.homework_1.hw03;


import java.util.*;

public class CustomList <T> implements List<T> {
    private int length;
    private T[] elements;


    public CustomList() {
        elements = (T[]) new Object[10];
        this.length = 0;
    }

    @Override
    public int size() {
        return this.length;
    }

    @Override
    public boolean isEmpty() {
        return this.length == 0;
    }

    @Override
    public boolean contains(Object o) {
        if(isEmpty()) {
            return false;
        } else {
            for(int i = 0; i < length; i++){
                if (elements[i].equals(o))
                    return true;
            }

            return false;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {

            private int i = 0;

            @Override
            public boolean hasNext() {
                return i  < length;
            }

            @Override
            public T next() {
                return get(i++);
            }

        };
    }

    @Override
    public Object[] toArray() {
       Object[] arr = new Object[length];
       System.arraycopy(elements, 0, arr, 0, length);
       return arr;
    }

    @Override
    public boolean add(Object object) {
        if(length >= elements.length) {
            increaseCapacity();
        }
        elements[length++] = (T) object;

        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if(index >= 0) {
            remove(index);
            return true;
        }

        return false;
    }

    @Override
    public boolean addAll(Collection c) {
        if(c.isEmpty()) {
            return false;
        }
        for(Object o : c) {
            add(o);
        }

        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if(c.isEmpty()) {
            return false;
        }
        for(T o : c) {
            add(index, o);
        }

        return true;
    }

    @Override
    public void clear() {
        elements = (T[]) new Object[10];
        length = 0;
    }

    @Override
    public T get(int index) {
        checkIndex(index);
        return elements[index];
    }

    @Override
    public Object set(int index, Object element) {
        checkIndex(index);
        T oldValue = elements[index];
        elements[index] = (T) element;

        return oldValue;
    }

    @Override
    public void add(int index, T element) {
        checkIndex(index);

        if(length == elements.length) {
            increaseCapacity();
        }
        moveRight(index);
        elements[index] = (T) element;
    }

    @Override
    public T remove(int index) {
        checkIndex(index);
        T oldValue = elements[index];
        moveLeft(index);

        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        return findIndexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return findLastIndexOf(o);
    }

    @Override
    public ListIterator listIterator() {
        return null;
    }

    @Override
    public ListIterator listIterator(int index) {
        return null;
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return List.of();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Iterator<T> iterator = iterator();

        while(iterator.hasNext()) {
            T item = iterator.next();
            if(!c.contains(item)){
                iterator.remove();

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for(Object o : c) {
            while(remove(o)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if(c.isEmpty()) {
            return false;
        }
        for(Object o : c) {
            if(!contains(o)){

                return false;
            }
        }

        return true;
    }

    @Override
    public Object[] toArray(Object[] a) {
        if(a.length < length) {
            return (Arrays.copyOf(elements,length, a.getClass()));
        }

        System.arraycopy(elements, 0, a, 0, length);

        if(a.length > length) {
            a[length] = null;
        }

        return a;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(elements),length);
    }

    private void increaseCapacity() {
        T[] tempElements = (T[]) new Object[length * 2];

        for(int i = 0; i < length; i++) {
            tempElements[i] = elements[i];
        }
        elements = tempElements;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index doesn't exist");
        }
    }

    private int findIndexOf(Object o) {
        if(o == null) {
            return -1;
        }
        for(int i = 0;i < length;i++) {
            if(elements[i].equals(o)) {
                return i;
            }
        }

        return -1;
    }

    private int findLastIndexOf(Object o) {
        if(o == null) {
            return -1;
        }

        int index = -1;

        for(int i = 0; i < length; i++) {
            if(elements[i].equals(o)) {
                index = i;
            }
        }

        return index;
    }

    private void moveRight(int index) {
        checkIndex(index);

        if(length == elements.length) {
            increaseCapacity();
        }

        for(int i = length;i > index;i--) {
            elements[i] = elements[i - 1];
        }
        length++;
    }

    private void moveLeft(int index) {
        checkIndex(index);

        for(int i = index; i < length - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[length - 1] = null;
        length--;
    }

}
