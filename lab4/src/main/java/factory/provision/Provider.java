package factory.provision;

import factory.InteractivePerformer;
import factory.Product;
import factory.Storage;

import java.util.logging.Level;

import static factory.Factory.fabricConditionDescriptor;
import static factory.Factory.logger;

public class Provider<T extends Product> implements InteractivePerformer {
    private volatile int period;
    private Thread executor;
    private final Storage<T> storage;
    private final Class<T> productClass;

    public Provider(int period, Class<T> productClass, Storage<T> storage, int providerSerialNumber) {
        this.period = period;
        this.productClass = productClass;
        this.storage = storage;
        this.executor = new Thread(new ManufacturingProcess());
        this.executor.setName("Provider of " + productClass.getSimpleName() + " #" + providerSerialNumber);
    }

    @Override
    public void startPerform() {
        executor.start();
    }

    @Override
    public void stopPerform() {
        executor.interrupt();
        try {
            executor.join(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void changePeriod(int newPeriod) {
        period = newPeriod;
    }

    private class ManufacturingProcess implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                long startTime = System.currentTimeMillis();
                doCreation();
                long elapsed = System.currentTimeMillis() - startTime;
                long sleepTime = period - elapsed;

                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }

        private void doCreation() {
            if (Thread.interrupted()) {
                return;
            }
            try {
                T product = productClass.getDeclaredConstructor().newInstance();
                storage.pushComponent(product);

                String productClassName = product.getClass().getSimpleName();
                switch (productClassName) {
                    case "Engine":
                        fabricConditionDescriptor.incrementEngineProductionAmount();
                        if (logger != null) {
                            logger.log(Level.INFO, "\033[95m Product " + productClassName + "<ID=" + product.getId() + "> was created. \u001b[0m");
                        }
                        break;
                    case "Accessory":
                        fabricConditionDescriptor.incrementAccessoryProductionAmount();
                        if (logger != null) {
                            logger.log(Level.INFO, "\u001b[34m Product " + productClassName + "<ID=" + product.getId() + "> was created. \u001b[0m");
                        }
                        break;
                    case "Body":
                        fabricConditionDescriptor.incrementBodyProductionAmount();
                        if (logger != null) {
                            logger.log(Level.INFO, "\u001b[32m Product " + productClassName + "<ID=" + product.getId() + "> was created. \u001b[0m");
                        }
                        break;
                    default:
                        if (logger != null) {
                            logger.log(Level.WARNING, "Unknown product type: " + productClassName);
                        }
                        break;
                }
            } catch (Exception e) {
                if (logger != null) {
                    logger.log(Level.SEVERE, "Error creating product: " + productClass.getSimpleName(), e);
                } else {
                    System.err.println("Error creating product: " + productClass.getSimpleName());
                    e.printStackTrace();
                }
            }
        }
    }
}
