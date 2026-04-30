package factory.factoryData;

import factory.Factory;
import factory.Package;

import java.util.concurrent.atomic.AtomicInteger;

public class FabricCondition implements Package {
    private final int bodyStorageCapacity;
    private final int engineStorageCapacity;
    private final int accessoryStorageCapacity;
    private final int carStorageCapacity;

    private final AtomicInteger carInQueue = new AtomicInteger(0);

    private int bodyProductionAmount = 0;
    private int engineProductionAmount = 0;
    private final AtomicInteger accessoryProductionAmount = new AtomicInteger(0);
    private final AtomicInteger carProductionAmount = new AtomicInteger(0);

    private final Factory factory;

    public FabricCondition(Factory factory) {
        this.factory = factory;
        bodyStorageCapacity = factory.getFabricInfo().storageBodySize();
        engineStorageCapacity = factory.getFabricInfo().storageMotorSize();
        accessoryStorageCapacity = factory.getFabricInfo().storageAccessorySize();
        carStorageCapacity = factory.getFabricInfo().storageAutoSize();
    }


    public int getAccessoryCurrCapacity() {
        return factory.getAccessoryStorage().getCurrentSize();
    }

    public int getCarCurrCapacity() {
        return factory.getCarStorage().getCurrentSize();
    }

    public int getEngineCurrCapacity() {
        return factory.getEngineStorage().getCurrentSize();
    }

    public int getBodyCurrCapacity() {
        return factory.getBodyStorage().getCurrentSize();
    }

    // Геттеры для вместимости складов (без изменений)
    public int getAccessoryStorageCapacity() {
        return accessoryStorageCapacity;
    }

    public int getBodyStorageCapacity() {
        return bodyStorageCapacity;
    }

    public int getCarStorageCapacity() {
        return carStorageCapacity;
    }

    public int getEngineStorageCapacity() {
        return engineStorageCapacity;
    }

    // Работа с очередью задач ThreadPool (без изменений)
    public void incrementCarsInQueue() {
        carInQueue.incrementAndGet();
    }

    public void decrementCarsInQueue() {
        carInQueue.decrementAndGet();
    }

    public int getCarsInQueue() {
        return carInQueue.get();
    }

    // Счетчики произведенных деталей (без изменений)
    public void incrementAccessoryProductionAmount() {
        accessoryProductionAmount.incrementAndGet();
    }

    public void incrementBodyProductionAmount() {
        ++bodyProductionAmount;
    }

    public void incrementCarProductionAmount() {
        carProductionAmount.incrementAndGet();
    }

    public void incrementEngineProductionAmount() {
        ++engineProductionAmount;
    }

    public int getAccessoryProductionAmount() {
        return accessoryProductionAmount.get();
    }

    public int getBodyProductionAmount() {
        return bodyProductionAmount;
    }

    public int getCarProductionAmount() {
        return carProductionAmount.get();
    }

    public int getEngineProductionAmount() {
        return engineProductionAmount;
    }
}
