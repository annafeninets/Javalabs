package operation;

import calculator.Calculator;
import calcException.OperationException;
import stack.MyStack;

public abstract class ArithmeticOperation<T> implements Operation<T> {
    @Override
    public void execute(Calculator<T> calculator) throws OperationException {
        apply(getStack(calculator));
    }

    protected abstract void apply(MyStack<T> stack) throws OperationException;
}
