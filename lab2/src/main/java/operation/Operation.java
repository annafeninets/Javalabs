package operation;

import calculator.Calculator;
import calcException.OperationException;
import stack.MyStack;
import runtimeContext.RuntimeContext;

public interface Operation<T> {
    void execute(Calculator<T> calculator) throws OperationException;
    default MyStack<T> getStack(Calculator<T> calculator) {
        return calculator.getStack();
    }
    default RuntimeContext<T> getContext(Calculator<T> calculator) {
        return calculator.getContext();
    }
}

