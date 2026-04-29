package operation;

import calculator.Calculator;
import calcException.OperationException;
import runtimeContext.RuntimeContext;

public abstract class ContextOperation<T> implements Operation<T> {
    @Override
    public void execute(Calculator<T> calculator) throws OperationException {
        apply(getContext(calculator));
    }

    protected abstract void apply(RuntimeContext<T> context) throws OperationException;
}

