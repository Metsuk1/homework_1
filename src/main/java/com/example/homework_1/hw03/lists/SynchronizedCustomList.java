package com.example.homework_1.hw03.lists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SynchronizedCustomList<T> extends CustomList<T> {
    private final CustomList<T> delegate;

    public SynchronizedCustomList(CustomList<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public synchronized boolean add(Object element) {
        delegate.add(element);

        return false;
    }

    @Override
    public synchronized void add(int index, T element) {
        delegate.add(index, element);
    }

    @Override
    public synchronized T get(int index) {
        return delegate.get(index);
    }

    @Override
    public synchronized T remove(int index) {
        return delegate.remove(index);
    }

    @Override
    public synchronized boolean remove(Object element) {
        return delegate.remove(element);
    }

    @Override
    public synchronized int size() {
        return delegate.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public synchronized void clear() {
        delegate.clear();
    }

    @Override
    public synchronized boolean contains(Object element) {
        return delegate.contains(element);
    }

    @Override
    public synchronized Iterator<T> iterator() {
        // Return a copy to avoid concurrent modification
        List<T> snapshot = new ArrayList<>();
        Iterator<T> it = delegate.iterator();
        while (it.hasNext()) {
            snapshot.add(it.next());
        }
        return snapshot.iterator();
    }
}