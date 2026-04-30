package factory.product;

import factory.Product;

import java.util.concurrent.atomic.AtomicInteger;

public class Engine implements Product {
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    public final int id;
    public Engine(){
        this.id=idCounter.incrementAndGet();
    }

    @Override
    public int getId() {
        return id;
    }
}