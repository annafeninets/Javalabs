package factory.storage;

import factory.Product;
import factory.Storage;
import java.util.LinkedList;

public class SingleSpeciesStorage<E extends Product> implements Storage<E> {
    private final LinkedList<E> store = new LinkedList<>();
    private final int capacity;

    public SingleSpeciesStorage(int capacity) {
        this.capacity = capacity;
    }

    @Override
    synchronized public E getComponent() {
        while (store.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        E component = store.poll();
        this.notifyAll();
        return component;
    }

    @Override
    synchronized public void pushComponent(E component) {
        while (store.size() >= capacity) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        store.offer(component);
        this.notifyAll();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    synchronized public int getCurrentSize() {
        return store.size();
    }
}
