package calculator;

import calcException.*;
import main.Main;
import operation.Operation;
import runtimeContext.RuntimeContextD;
import stack.MyStack;

import java.util.logging.Level;

public class CalculatorDouble implements Calculator<Double> {
    private final MyStack<Double> stack = new MyStack<>();
    private final RuntimeContextD context = new RuntimeContextD();

    @Override
    public void execute(Operation<Double> operation) {
        Main.logger.log(Level.INFO, operation.toString() + " starts to execute by calculator.");
        try {
            operation.execute(this);
        } catch (OperationException e) {
            System.out.println(e.getMessage());
            Main.logger.log(Level.WARNING, operation.toString() + "---" + e.getMessage());
        }
    }

    @Override
    public MyStack<Double> getStack() {
        return stack;
    }

    @Override
    public RuntimeContextD getContext() {
        return context;
    }
}
