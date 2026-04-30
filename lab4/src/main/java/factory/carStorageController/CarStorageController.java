package factory.carStorageController;

import factory.Storage;
import factory.product.Accessory;
import factory.product.Body;
import factory.product.Car;
import factory.job.CreateCar;
import factory.product.Engine;
import threadpol.PoolExecutor;

import static factory.Factory.fabricConditionDescriptor;

public class CarStorageController extends Thread {
    private final Storage<Car> carStorage;
    private final Storage<Body> bodyStorage;
    private final Storage<Engine> engineStorage;
    private final Storage<Accessory> accessoryStorage;
    private final PoolExecutor workers;
    private final Object saleMonitor;

    private static final double PRODUCTION_THRESHOLD = 0.5;

    public CarStorageController(Storage<Car> carStorage, Storage<Engine> engineStorage, Storage<Body> bodyStorage,
                                Storage<Accessory> accessoryStorage, PoolExecutor workers, Object saleMonitor) {
        this.carStorage = carStorage;
        this.bodyStorage = bodyStorage;
        this.engineStorage = engineStorage;
        this.accessoryStorage = accessoryStorage;
        this.workers = workers;
        this.saleMonitor = saleMonitor;
        this.setName("Car storage controller thread");

        int initialTasks = (int) (carStorage.getCapacity() * PRODUCTION_THRESHOLD);
        for (int i = 0; i < initialTasks; ++i) {
            workers.execute(new CreateCar(engineStorage, bodyStorage, accessoryStorage, carStorage));
        }
    }

    @Override
    public void run() {
        synchronized (saleMonitor) {
            while (!Thread.interrupted()) {
                try {
                    saleMonitor.wait();
                } catch (InterruptedException e) {
                    break;
                }

                int currentCarCount;
                int maxCapacity;
                int tasksInQueue;

                synchronized (carStorage) {
                    currentCarCount = carStorage.getCurrentSize();
                    maxCapacity = carStorage.getCapacity();
                    tasksInQueue = fabricConditionDescriptor.getCarsInQueue();
                }

                int targetCount = (int) (maxCapacity * PRODUCTION_THRESHOLD);
                int needed = targetCount - (currentCarCount + tasksInQueue);

                for (int i = 0; i < needed; i++) {
                    workers.execute(new CreateCar(engineStorage, bodyStorage, accessoryStorage, carStorage));
                }
            }
        }
    }
}