package factory.product;

import factory.Product;

import java.util.concurrent.atomic.AtomicInteger;

public class Accessory implements Product {
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    public final int id;
    public Accessory(){
        this.id=idCounter.incrementAndGet();
    }

    @Override
    public int getId() {
        return id;
    }
}