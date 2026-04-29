package operation;

import calcException.NotDefined;
import calcException.NotEnoughOperandToConfigure;
import calcException.OperationConfigurationError;
import calcException.OperationException;
import calcException.StackException;
import main.Main;
import runtimeContext.RuntimeContext;
import stack.MyStack;

import java.util.Objects;
import java.util.logging.Level;

public class PushD extends StackOperation<Double> implements CustomizableOperation {
    private String arg;

    @Override
    protected void apply(MyStack<Double> stack, RuntimeContext<Double> context)
            throws StackException, OperationException {
        try {
            Double defined = context.getIfDefined(arg);
            stack.push(Objects.requireNonNullElseGet(defined, () -> Double.parseDouble(arg)));
        } catch (NumberFormatException e) {
            throw new NotDefined(this, arg);
        }
    }

    @Override
    public void set(String[] option) throws OperationConfigurationError {
        if (option.length < 2) {
            throw new NotEnoughOperandToConfigure(this);
        } else {
            arg = option[1];
            Main.logger.log(Level.INFO, this + " is configured by " + arg);
        }
    }
}
/**package operation;

import calcException.NotDefined;
import calcException.NotEnoughOperandToConfigure;
import calcException.OperationConfigurationError;
import main.Main;
import runtimeContext.RuntimeContext;
import stack.MyStack;

import java.util.Objects;
import java.util.logging.Level;
public class PushD extends StackOperation<Double> implements CustomizableOperation {
    private String arg;

    @Override
    protected void apply(MyStack<Double> stack, RuntimeContext<Double> context) throws NotDefined {
        try {
            Double defined = context.getIfDefined(arg);
            stack.push(Objects.requireNonNullElseGet(defined, () -> Double.parseDouble(arg)));
        } catch (NumberFormatException e) {
            throw new NotDefined(this, arg);
        }
    }

    @Override
    public void set(String[] option) throws OperationConfigurationError {
        if (option.length < 2) {
            throw new NotEnoughOperandToConfigure(this);
        } else {
            arg = option[1];
            Main.logger.log(Level.INFO, this + " is configured by " + arg);
        }
    }
}*/
/**public class PushD implements StackOperation<Double>, CustomizableOperation {
    private String arg;

    @Override
    public void apply(MyStack<Double> stack, RuntimeContext<Double> context) throws NotDefined {
        try {
            Double defined = context.getIfDefined(arg);
            stack.push(Objects.requireNonNullElseGet(defined, () -> Double.parseDouble(arg)));
        } catch (NumberFormatException e){
            throw new NotDefined(this, arg);
        }
    }

    @Override
    public void set(String[] option) throws NotEnoughOperandToConfigure {
        if(option.length < 2){
            throw new NotEnoughOperandToConfigure(this);
        } else {
            arg = option[1];
            Main.logger.log(Level.INFO, this+" is configured by "+arg);
        }
    }
}*/