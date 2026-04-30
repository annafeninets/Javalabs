package factory.product;

import factory.Product;

import java.util.concurrent.atomic.AtomicInteger;

public class Body implements Product {
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    public final int id;
    public Body(){
        this.id=idCounter.incrementAndGet();
    }

    @Override
    public int getId() {
        return id;
    }
}