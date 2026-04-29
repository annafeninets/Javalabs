package calculator;

import operation.Operation;
import calcException.OperationException;
import stack.MyStack;
import runtimeContext.RuntimeContext;

public interface Calculator<T> {
    void execute(Operation<T> operation) throws OperationException;

    MyStack<T> getStack();
    RuntimeContext<T> getContext();
}
