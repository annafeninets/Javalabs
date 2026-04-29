package operation;

import calculator.Calculator;
import calcException.OperationException;
import calcException.StackException;
import runtimeContext.RuntimeContext;
import stack.MyStack;

public abstract class StackOperation<T> implements Operation<T> {
    @Override
    public void execute(Calculator<T> calculator) throws OperationException {
        try {
            apply(getStack(calculator), getContext(calculator));
        } catch (StackException e) {
            throw new OperationException(this, e);
        } catch (OperationException e) {
            throw e;
        } catch (Exception e) {
            throw new OperationException(this, e);
        }
    }

    protected abstract void apply(MyStack<T> stack, RuntimeContext<T> context)
            throws StackException, OperationException;
}
