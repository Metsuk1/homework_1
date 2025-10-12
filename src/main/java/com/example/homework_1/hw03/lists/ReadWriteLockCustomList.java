package com.example.homework_1.hw03.lists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockCustomList<T> extends CustomList<T> {
    private final CustomList<T> delegate;
    private final ReadWriteLock rwLock;

    public ReadWriteLockCustomList(CustomList<T> delegate) {
        this.delegate = delegate;
        this.rwLock = new ReentrantReadWriteLock();
    }

    @Override
    public boolean add(Object element) {
        rwLock.writeLock().lock();
        try {
            delegate.add(element);
        } finally {
            rwLock.writeLock().unlock();
        }

        return false;
    }

    @Override
    public void add(int index, T element) {
        rwLock.writeLock().lock();
        try {
            delegate.add(index, element);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public T get(int index) {
        rwLock.readLock().lock();
        try {
            return delegate.get(index);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public T remove(int index) {
        rwLock.writeLock().lock();
        try {
            return delegate.remove(index);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public boolean remove(Object element) {
        rwLock.writeLock().lock();
        try {
            return delegate.remove(element);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public int size() {
        rwLock.readLock().lock();
        try {
            return delegate.size();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        rwLock.readLock().lock();
        try {
            return delegate.isEmpty();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        rwLock.writeLock().lock();
        try {
            delegate.clear();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public boolean contains(Object element) {
        rwLock.readLock().lock();
        try {
            return delegate.contains(element);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public Iterator<T> iterator() {
        rwLock.readLock().lock();
        try {
            // Return a snapshot to avoid concurrent modification
            List<T> snapshot = new ArrayList<>();
            Iterator<T> it = delegate.iterator();
            while (it.hasNext()) {
                snapshot.add(it.next());
            }
            return snapshot.iterator();
        } finally {
            rwLock.readLock().unlock();
        }
    }
}