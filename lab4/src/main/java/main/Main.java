package main;
import controller.FabricController;
import factory.Factory;
import gui.FactoryGUI;

public class Main {
    public static void main(String[] args) {
        Factory factory = new Factory();
        FabricController controller = new FabricController(factory);
        FactoryGUI gui = new FactoryGUI(controller, factory);
        factory.attach(gui);
    }
}