package factory.dealer;

import factory.InteractivePerformer;
import factory.Storage;
import factory.product.Car;

import java.util.logging.Level;

import static factory.Factory.logger;

public class Dealer implements InteractivePerformer {
    private volatile int period;
    private int dealerIndexNumber;
    private Thread executor;
    private final Storage<Car> storage;
    private final Object saleMonitor;

    public Dealer(int period, Storage<Car> storage, int dealerIndexNumber, Object saleMonitor) {
        this.period = period;
        this.storage = storage;
        this.saleMonitor = saleMonitor;
        this.dealerIndexNumber = dealerIndexNumber;
        this.executor = new Thread(new Task());
        this.executor.setName("Dealer thread " + dealerIndexNumber);
    }

    public Dealer(int period, Storage<Car> storage, Object saleMonitor) {
        this(period, storage, 0, saleMonitor);
    }

    @Override
    public void startPerform() {
        executor.start();
    }

    @Override
    public void stopPerform() {
        executor.interrupt();
    }

    @Override
    public void changePeriod(int newPeriod) {
        period = newPeriod;
    }

    private class Task implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(period);
                    doTask();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        private void doTask() {
            Car car = storage.getComponent();
            if (car != null) {
                synchronized (saleMonitor) {
                    saleMonitor.notify();
                }
                if (logger != null) {
                    logger.log(Level.INFO, "New car was bought. Dealer " + dealerIndexNumber +
                            ": Auto " + car.getId() + " (Body: " + car.getBodyId() + ", Motor: " + car.getEngineId() +
                            ", Accessory: " + car.getAccessoryId() + ")");
                }
            }
        }
    }
}