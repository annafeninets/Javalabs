package factory.product;

import factory.Product;

import java.util.concurrent.atomic.AtomicInteger;

public class Car implements Product {
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    private final int id;
    private final Engine engine;
    private final Body body;
    private final Accessory accessory;

    public Car(Engine engine, Body body, Accessory accessory) {
        this.id=idCounter.incrementAndGet();
        this.engine = engine;
        this.body = body;
        this.accessory = accessory;
    }

    @Override
    public int getId() {
        return id;
    }
    public int getAccessoryId() {
        return accessory.getId();
    }
    public int getBodyId() {
        return body.getId();
    }
    public int getEngineId() {
        return engine.getId();
    }
}